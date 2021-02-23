library service_tester;

import 'dart:async';
import 'dart:developer';

import 'package:dokit/kit/apm/version.dart';
import 'package:flutter/material.dart';
import 'package:package_info/package_info.dart';
import 'package:vm_service/utils.dart';
import 'package:vm_service/vm_service.dart';
import 'package:vm_service/vm_service_io.dart';

class VmHelper {
  VmHelper._privateConstructor() {}

  static final VmHelper _instance = VmHelper._privateConstructor();

  static VmHelper get instance => _instance;

  VmService serviceClient;
  Version _protocolVersion;
  Version _dartIoVersion;
  VM vm;

  // flutter版本
  String _flutterVersion = '';

  // 各Isolate内存使用情况
  Map<IsolateRef, MemoryUsage> memoryInfo = <IsolateRef, MemoryUsage>{};
  bool connected;
  AllocationProfile allocationProfile;
  PackageInfo packageInfo;

  Map<String, List<String>> get registeredMethodsForService =>
      _registeredMethodsForService;
  final Map<String, List<String>> _registeredMethodsForService =
      <String, List<String>>{};

  Future<void> startConnect() async {
    final ServiceProtocolInfo info = await Service.getInfo();
    if (info == null || info.serverUri == null) {
      print('service  protocol url is null,start vm service fail');
      return;
    }
    final Uri uri = convertToWebSocketUrl(serviceProtocolUrl: info.serverUri);
    serviceClient = await vmServiceConnectUri(uri.toString(), log: StdoutLog());
    print('socket connected in service $info');
    connected = true;

    vm = await serviceClient.getVM();
    final List<IsolateRef> isolates = vm.isolates;
    for (final IsolateRef element in isolates) {
      final MemoryUsage memoryUsage =
          await serviceClient.getMemoryUsage(element.id);
      memoryInfo[element] = memoryUsage;
    }

    loadExtensionService();
    PackageInfo.fromPlatform().then((PackageInfo value) => packageInfo = value);
  }

  // 获取flutter版本，目前比较鸡肋，需要借助devtools向vmservice注册的服务来获取,flutter 未 attach的情况下无法使用。
  Future<void> loadExtensionService() async {
    final String serviceStreamName = await this.serviceStreamName;
    serviceClient.onEvent(serviceStreamName).listen(handleServiceEvent);
    final List<String> streamIds = [
      EventStreams.kDebug,
      EventStreams.kExtension,
      EventStreams.kGC,
      EventStreams.kIsolate,
      EventStreams.kLogging,
      EventStreams.kStderr,
      EventStreams.kStdout,
      EventStreams.kTimeline,
      EventStreams.kVM,
      serviceStreamName,
    ];

    await Future.wait(streamIds.map((String id) async {
      try {
        await serviceClient.streamListen(id);
      } catch (e) {
        print(e);
      }
    }));
    resolveFlutterVersion();

    return Future<void>.value();
  }

  String get flutterVersion {
    if (_flutterVersion != '') {
      return _flutterVersion;
    } else {
      return 'Flutter Attach后可获取版本号';
    }
  }

  Future<String> get serviceStreamName async =>
      (await isProtocolVersionSupported(
              supportedVersion: SemanticVersion(major: 3, minor: 22)))
          ? 'Service'
          : '_Service';

  Future<bool> isProtocolVersionSupported({
    @required SemanticVersion supportedVersion,
  }) async {
    _protocolVersion ??= await serviceClient.getVersion();
    return isProtocolVersionSupportedNow(supportedVersion: supportedVersion);
  }

  bool isProtocolVersionSupportedNow({
    @required SemanticVersion supportedVersion,
  }) {
    return _versionSupported(
      version: _protocolVersion,
      supportedVersion: supportedVersion,
    );
  }

  bool _versionSupported({
    @required Version version,
    @required SemanticVersion supportedVersion,
  }) {
    return SemanticVersion(
      major: version.major,
      minor: version.minor,
    ).isSupported(supportedVersion: supportedVersion);
  }

  Future<bool> isDartIoVersionSupported({
    @required SemanticVersion supportedVersion,
    @required String isolateId,
  }) async {
    _dartIoVersion ??= await getDartIOVersion(isolateId);
    return _versionSupported(
      version: _dartIoVersion,
      supportedVersion: supportedVersion,
    );
  }

  Future<Version> getDartIOVersion(String isolateId) =>
      serviceClient.getDartIOVersion(isolateId);

  void handleServiceEvent(Event e) {
    if (e.kind == EventKind.kServiceRegistered) {
      final String serviceName = e.service;
      _registeredMethodsForService
          .putIfAbsent(serviceName, () => [])
          .add(e.method);
      if (_flutterVersion == '' && serviceName == 'flutterVersion') {
        resolveFlutterVersion();
      }
    }

    if (e.kind == EventKind.kServiceUnregistered) {
      final String serviceName = e.service;
      _registeredMethodsForService.remove(serviceName);
    }
  }

  void resolveFlutterVersion() {
    callMethod('flutterVersion')?.then((Response value) =>
        _flutterVersion = FlutterVersion.parse(value.json).version);
  }

  Future<Response> callMethod(String method) {
    if (registeredMethodsForService.containsKey(method)) {
      return serviceClient.callMethod(registeredMethodsForService[method].last,
          isolateId: vm.isolates.first.id);
    }
    return null;
  }

  void updateMemoryUsage() {
    if (serviceClient != null && connected) {
      final List<IsolateRef> isolates = vm.isolates;
      for (final IsolateRef element in isolates) {
        serviceClient
            .getMemoryUsage(element.id)
            .then((MemoryUsage value) => memoryInfo[element] = value);
      }
    }
  }

  void dumpAllocationProfile() {
    if (serviceClient != null && connected) {
      serviceClient
          .getAllocationProfile(vm.isolates.first.id)
          .then((AllocationProfile value) => allocationProfile = value);
    }
  }

  Future<void> disConnect() async {
    if (serviceClient != null) {
      print('waiting for client to shut down...');
      serviceClient.dispose();

      await serviceClient.onDone;
      connected = false;
      serviceClient = null;
      print('service client shut down');
    }

    return Future<void>.value();
  }
}

class StdoutLog extends Log {
  @override
  void warning(String message) => print(message);

  @override
  void severe(String message) => print(message);
}
