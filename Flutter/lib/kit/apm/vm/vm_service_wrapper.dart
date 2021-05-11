import 'dart:developer';

import 'package:dokit/kit/apm/vm/version.dart';
import 'package:vm_service/utils.dart';
import 'package:vm_service/vm_service.dart';
import 'package:vm_service/vm_service_io.dart';

class VMServiceWrapper {
  VMServiceWrapper._privateConstructor();

  static final VMServiceWrapper _instance =
      VMServiceWrapper._privateConstructor();

  static VMServiceWrapper get instance => _instance;

  VmService? service;

  IsolateRef? main;

  VM? vm;

  ExtensionService? _extensionService;
  bool connected = false;

  Future<VmService> getService(info) async {
    Uri uri = convertToWebSocketUrl(serviceProtocolUrl: info.serverUri);
    return await vmServiceConnectUri(uri.toString(), log: StdoutLog());
  }

  Future<void> connect() async {
    ServiceProtocolInfo info = await Service.getInfo();
    if (info.serverUri == null) {
      print("service  protocol url is null,start vm service fail");
      return;
    }
    service = await getService(info);
    print('socket connected in service $info');
    vm = await service?.getVM();
    List<IsolateRef>? isolates = vm?.isolates;
    main = isolates?.firstWhere((ref) => ref.name?.contains('main') == true);
    main ??= isolates?.first;
    connected = true;
  }

  Future<Response?> callExtensionService(String method) async {
    if (_extensionService == null && service != null && main != null) {
      _extensionService = ExtensionService(service!, main!);
      await _extensionService?.loadExtensionService();
    }
    return _extensionService!.callMethod(method);
  }

  gc() {
    var isolateId = main?.id;
    if (connected && isolateId != null) {
      service?.getAllocationProfile(isolateId, gc: true);
    }
  }

  disConnect() async {
    if (service != null) {
      print('waiting for client to shut down...');
      await service?.dispose();

      await service?.onDone;
      connected = false;
      service = null;
      print('service client shut down');
    }
  }
}

class ExtensionService {
  final VmService serviceClient;
  final IsolateRef main;
  Version? _protocolVersion;
  Version? _dartIoVersion;

  Map<String, List<String>> get registeredMethodsForService =>
      _registeredMethodsForService;
  final Map<String, List<String>> _registeredMethodsForService = {};

  ExtensionService(this.serviceClient, this.main);

  // 获取flutter版本，目前比较鸡肋，需要借助devtools向vmservice注册的服务来获取,flutter 未 attach的情况下无法使用。
  Future<void> loadExtensionService() async {
    final serviceStreamName = await this.serviceStreamName;
    serviceClient.onEvent(serviceStreamName).listen(handleServiceEvent);
    final streamIds = [
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
  }

  Future<String> get serviceStreamName async =>
      (await isProtocolVersionSupported(
              supportedVersion: SemanticVersion(major: 3, minor: 22)))
          ? 'Service'
          : '_Service';

  Future<bool> isProtocolVersionSupported({
    required SemanticVersion supportedVersion,
  }) async {
    _protocolVersion ??= await serviceClient.getVersion();
    return isProtocolVersionSupportedNow(supportedVersion: supportedVersion);
  }

  bool isProtocolVersionSupportedNow({
    required SemanticVersion supportedVersion,
  }) {
    if (_protocolVersion == null) {
      return false;
    }
    return _versionSupported(
      version: _protocolVersion!,
      supportedVersion: supportedVersion,
    );
  }

  bool _versionSupported({
    required Version version,
    required SemanticVersion supportedVersion,
  }) {
    return SemanticVersion(
      major: version.major ?? 0,
      minor: version.minor ?? 0,
    ).isSupported(supportedVersion: supportedVersion);
  }

  Future<bool> isDartIoVersionSupported({
    required SemanticVersion supportedVersion,
    required String isolateId,
  }) async {
    _dartIoVersion ??= await getDartIOVersion(isolateId);
    return _versionSupported(
      version: _dartIoVersion!,
      supportedVersion: supportedVersion,
    );
  }

  Future<Version> getDartIOVersion(String isolateId) =>
      serviceClient.getDartIOVersion(isolateId);

  void handleServiceEvent(Event e) {
    if (e.kind == EventKind.kServiceRegistered && e.method != null) {
      final serviceName = e.service ?? '';
      _registeredMethodsForService
          .putIfAbsent(serviceName, () => [])
          .add(e.method!);
    }

    if (e.kind == EventKind.kServiceUnregistered) {
      final serviceName = e.service;
      _registeredMethodsForService.remove(serviceName);
    }
  }

  Future<Response?> callMethod(String method) {
    if (registeredMethodsForService.containsKey(method)) {
      return (serviceClient.callMethod(
          registeredMethodsForService[method]!.last,
          isolateId: main.id));
    }
    return Future.value(null);
  }
}

class StdoutLog extends Log {
  void warning(String message) => print(message);

  void severe(String message) => print(message);
}
