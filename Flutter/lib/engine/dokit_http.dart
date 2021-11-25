import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:dokit/kit/apm/apm.dart';
import 'package:dokit/kit/apm/http_kit.dart';

class DoKitHttpOverrides extends HttpOverrides {
  DoKitHttpOverrides(this.origin);

  final HttpOverrides? origin;

  @override
  HttpClient createHttpClient(SecurityContext? context) {
    if (origin != null) {
      return DoKitHttpClient(origin!.createHttpClient(context));
    }
    HttpOverrides.global = null;
    final HttpClient client = DoKitHttpClient(HttpClient(context: context));
    HttpOverrides.global = this;
    return client;
  }
}

class DoKitHttpClient implements HttpClient {
  DoKitHttpClient(this.origin);

  final HttpClient origin;
  HttpInfo? httpInfo;

  @override
  set autoUncompress(bool value) => origin.autoUncompress = value;

  @override
  bool get autoUncompress => origin.autoUncompress;

  @override
  set idleTimeout(Duration value) => origin.idleTimeout = value;

  @override
  Duration get idleTimeout => origin.idleTimeout;

  @override
  set connectionTimeout(Duration? value) => origin.connectionTimeout = value;

  @override
  Duration? get connectionTimeout => origin.connectionTimeout;

  @override
  set maxConnectionsPerHost(int? value) => origin.maxConnectionsPerHost = value;

  @override
  int? get maxConnectionsPerHost => origin.maxConnectionsPerHost;

  @override
  set userAgent(String? value) => origin.userAgent = value;

  @override
  String? get userAgent => origin.userAgent;

  @override
  void addCredentials(
      Uri url, String realm, HttpClientCredentials credentials) {
    origin.addCredentials(url, realm, credentials);
  }

  @override
  void addProxyCredentials(
      String host, int port, String realm, HttpClientCredentials credentials) {
    origin.addProxyCredentials(host, port, realm, credentials);
  }

  @override
  set authenticate(
      Future<bool> Function(Uri url, String scheme, String realm)? f) {
    origin.authenticate = f as Future<bool> Function(Uri url, String scheme, String? realm);
  }

  @override
  set authenticateProxy(
      Future<bool> Function(String host, int port, String scheme, String realm)?
          f) {
    origin.authenticateProxy = f as Future<bool> Function(String host, int port, String scheme, String? realm);
  }

  @override
  set badCertificateCallback(
      bool Function(X509Certificate cert, String host, int port)? callback) {
    origin.badCertificateCallback = callback;
  }

  @override
  void close({bool force = false}) {
    origin.close(force: force);
  }

  @override
  set findProxy(String Function(Uri url)? f) {
    origin.findProxy = f;
  }

  Future<HttpClientRequest> monitor(Future<HttpClientRequest> future) async {
    future = future.catchError((dynamic error, [StackTrace? stackTrace]) {
      if (httpInfo == null) {
        httpInfo = HttpInfo.error(error.toString());
        final HttpKit? kit = ApmKitManager.instance.getKit(ApmKitName.KIT_HTTP);
        kit?.save(httpInfo);
      }
    });
    final HttpClientRequest request = await future;
    httpInfo ??= HttpInfo(request.uri, request.method);
    final HttpKit? kit = ApmKitManager.instance.getKit(ApmKitName.KIT_HTTP);
    kit?.save(httpInfo);
    return DoKitHttpClientRequest(request, httpInfo);
  }

  void addRequestBody(HttpClientRequest request) {
    if (request.method.toUpperCase() != 'GET') {}
  }

  @override
  Future<HttpClientRequest> delete(String host, int port, String path) {
    return monitor(origin.delete(host, port, path));
  }

  @override
  Future<HttpClientRequest> deleteUrl(Uri url) {
    return monitor(origin.deleteUrl(url));
  }

  @override
  Future<HttpClientRequest> get(String host, int port, String path) {
    return monitor(origin.get(host, port, path));
  }

  @override
  Future<HttpClientRequest> getUrl(Uri url) {
    return monitor(origin.getUrl(url));
  }

  @override
  Future<HttpClientRequest> head(String host, int port, String path) {
    return monitor(origin.head(host, port, path));
  }

  @override
  Future<HttpClientRequest> headUrl(Uri url) {
    return monitor(origin.headUrl(url));
  }

  @override
  Future<HttpClientRequest> open(
      String method, String host, int port, String path) {
    return monitor(origin.open(method, host, port, path));
  }

  @override
  Future<HttpClientRequest> openUrl(String method, Uri url) {
    return monitor(origin.openUrl(method, url));
  }

  @override
  Future<HttpClientRequest> patch(String host, int port, String path) {
    return monitor(origin.patch(host, port, path));
  }

  @override
  Future<HttpClientRequest> patchUrl(Uri url) {
    return monitor(origin.patchUrl(url));
  }

  @override
  Future<HttpClientRequest> post(String host, int port, String path) {
    return monitor(origin.post(host, port, path));
  }

  @override
  Future<HttpClientRequest> postUrl(Uri url) {
    return monitor(origin.postUrl(url));
  }

  @override
  Future<HttpClientRequest> put(String host, int port, String path) {
    return monitor(origin.put(host, port, path));
  }

  @override
  Future<HttpClientRequest> putUrl(Uri url) {
    return monitor(origin.postUrl(url));
  }
}

class DoKitHttpClientRequest implements HttpClientRequest {
  DoKitHttpClientRequest(this.origin, this.httpInfo);

  final HttpClientRequest origin;
  final HttpInfo? httpInfo;

  @override
  bool get bufferOutput => origin.bufferOutput;

  @override
  set bufferOutput(bool value) => origin.bufferOutput = value;

  @override
  int get contentLength => origin.contentLength;

  @override
  set contentLength(int value) => origin.contentLength = value;

  @override
  Encoding get encoding => origin.encoding;

  @override
  set encoding(Encoding value) => origin.encoding = value;

  @override
  bool get followRedirects => origin.followRedirects;

  @override
  set followRedirects(bool value) => origin.followRedirects = value;

  @override
  int get maxRedirects => origin.maxRedirects;

  @override
  set maxRedirects(int value) => origin.maxRedirects = value;

  @override
  set persistentConnection(bool value) => origin.persistentConnection = value;

  @override
  bool get persistentConnection => origin.persistentConnection;

  @override
  HttpHeaders get headers => origin.headers;

  @override
  String get method => origin.method;

  @override
  Uri get uri => origin.uri;

  @override
  HttpConnectionInfo? get connectionInfo => origin.connectionInfo;

  @override
  List<Cookie> get cookies => origin.cookies;

  @override
  Future<HttpClientResponse> get done => origin.done;

  @override
  void write(Object? obj) {
    origin.write(obj);
  }

  @override
  void writeAll(Iterable<dynamic> objects, [String separator = '']) {
    origin.writeAll(objects, separator);
  }

  @override
  void writeCharCode(int charCode) {
    origin.writeCharCode(charCode);
  }

  @override
  void writeln([dynamic obj = '']) {
    origin.writeln(obj);
  }

  @override
  void add(List<int> data) {
    origin.add(data);
    recordParameter(data);
  }

  void recordParameter(List<int> data) {
    try {
      httpInfo?.request.header = headers.toString();
      httpInfo?.request.add(encoding.decode(data));
    } catch (e) {
      print(e);
    }
  }

  @override
  void addError(Object error, [StackTrace? stackTrace]) {
    origin.addError(error, stackTrace);
  }

  @override
  Future<dynamic> addStream(Stream<List<int>> stream) {
    stream = stream.asBroadcastStream();
    stream.listen((List<int> event) {
      recordParameter(event);
    });
    return origin.addStream(stream);
  }

  @override
  Future<HttpClientResponse> close() {
    return monitor(origin.close());
  }

  Future<HttpClientResponse> monitor(Future<HttpClientResponse> future) async {
    final HttpClientResponse response = await future;

    return DoKitHttpClientResponse(response, recordResponse);
  }

  void recordResponse(int code, String result, String header, int size) {
    httpInfo?.response.update(code, result, header, size);
  }

  @override
  Future<dynamic> flush() {
    return origin.flush();
  }

  @override
  void abort([Object? exception, StackTrace? stackTrace]) {
    return origin.abort(exception, stackTrace);
  }
}

extension HttpClientRequestExt on HttpClientRequest {
  void abort([Object? exception, StackTrace? stackTrace]) {
    this.abort(exception, stackTrace);
  }
}

class DoKitHttpClientResponse implements HttpClientResponse {
  DoKitHttpClientResponse(this.origin, this.recordResponse);

  final HttpClientResponse origin;
  final Function(int, String, String, int) recordResponse;

  @override
  Future<bool> any(bool Function(List<int> element) test) {
    return origin.any(test);
  }

  @override
  Stream<List<int>> asBroadcastStream(
      {void Function(StreamSubscription<List<int>> subscription)? onListen,
      void Function(StreamSubscription<List<int>> subscription)? onCancel}) {
    return origin.asBroadcastStream(onListen: onListen, onCancel: onCancel);
  }

  @override
  Stream<E> asyncExpand<E>(Stream<E>? Function(List<int> event) convert) {
    return asyncExpand(convert);
  }

  @override
  Stream<E> asyncMap<E>(FutureOr<E> Function(List<int> event) convert) {
    return asyncMap(convert);
  }

  @override
  Stream<R> cast<R>() {
    return origin.cast();
  }

  @override
  X509Certificate? get certificate => origin.certificate;

  @override
  HttpClientResponseCompressionState get compressionState =>
      origin.compressionState;

  @override
  HttpConnectionInfo? get connectionInfo => origin.connectionInfo;

  @override
  Future<bool> contains(dynamic needle) {
    return origin.contains(needle);
  }

  @override
  int get contentLength => origin.contentLength;

  @override
  List<Cookie> get cookies => origin.cookies;

  @override
  Future<Socket> detachSocket() {
    return origin.detachSocket();
  }

  @override
  Stream<List<int>> distinct(
      [bool Function(List<int> previous, List<int> next)? equals]) {
    return origin.distinct(equals);
  }

  @override
  Future<E> drain<E>([E? futureValue]) {
    return origin.drain(futureValue);
  }

  @override
  Future<List<int>> elementAt(int index) {
    return elementAt(index);
  }

  @override
  Future<bool> every(bool Function(List<int> element) test) {
    return origin.every(test);
  }

  @override
  Stream<S> expand<S>(Iterable<S> Function(List<int> element) convert) {
    return origin.expand(convert);
  }

  @override
  Future<List<int>> get first => origin.first;

  @override
  Future<List<int>> firstWhere(bool Function(List<int> element) test,
      {List<int> Function()? orElse}) {
    return origin.firstWhere(test, orElse: orElse);
  }

  @override
  Future<S> fold<S>(
      S initialValue, S Function(S previous, List<int> element) combine) {
    return origin.fold(initialValue, combine);
  }

  @override
  Future<dynamic> forEach(void Function(List<int> element) action) {
    return origin.forEach(action);
  }

  @override
  Stream<List<int>> handleError(Function onError,
      {bool Function(dynamic error)? test}) {
    return origin.handleError(onError, test: test);
  }

  @override
  HttpHeaders get headers => origin.headers;

  @override
  bool get isBroadcast => origin.isBroadcast;

  @override
  Future<bool> get isEmpty => origin.isEmpty;

  @override
  bool get isRedirect => origin.isRedirect;

  @override
  Future<String> join([String separator = '']) {
    return origin.join(separator);
  }

  @override
  Future<List<int>> get last => origin.last;

  @override
  Future<List<int>> lastWhere(bool Function(List<int> element) test,
      {List<int> Function()? orElse}) {
    return origin.lastWhere(test, orElse: orElse);
  }

  @override
  Future<int> get length => origin.length;

  bool isTextResponse() {
    return headers['content-type'] != null &&
        (headers['content-type'].toString().contains('json') ||
            headers['content-type'].toString().contains('text') ||
            headers['content-type'].toString().contains('xml'));
  }

  Encoding? getEncoding() {
    String charset;
    if (headers.contentType != null && headers.contentType?.charset != null) {
      charset = headers.contentType!.charset!;
    } else {
      charset = 'utf-8';
    }
    return Encoding?.getByName(charset);
  }

  @override
  StreamSubscription<List<int>> listen(void Function(List<int> event)? onData,
      {Function? onError, void Function()? onDone, bool? cancelOnError}) {
    if (!isTextResponse()) {
      recordResponse(
          statusCode, '返回结果不支持解析', headers.toString(), contentLength);
      return origin.listen(onData,
          onError: onError, onDone: onDone, cancelOnError: cancelOnError);
    }
    void onDataWrapper(List<int> result) {
      onData?.call(result);
      try {
        final encoding = getEncoding();
        if (encoding != null) {
          recordResponse(statusCode, encoding.decode(result),
              headers.toString(), contentLength);
        } else {
          recordResponse(
              statusCode, '返回结果解析失败', headers.toString(), contentLength);
        }
      } catch (e) {
        recordResponse(
            statusCode, '返回结果解析失败', headers.toString(), contentLength);
      }
    }

    return origin.listen(onDataWrapper,
        onError: onError, onDone: onDone, cancelOnError: cancelOnError);
  }

  @override
  Stream<S> map<S>(S Function(List<int> event) convert) {
    return origin.map(convert);
  }

  @override
  bool get persistentConnection => origin.persistentConnection;

  @override
  Future<dynamic> pipe(StreamConsumer<List<int>> streamConsumer) {
    return origin.pipe(streamConsumer);
  }

  @override
  String get reasonPhrase => origin.reasonPhrase;

  @override
  Future<HttpClientResponse> redirect(
      [String? method, Uri? url, bool? followLoops]) {
    return origin.redirect(method, url, followLoops);
  }

  @override
  List<RedirectInfo> get redirects => origin.redirects;

  @override
  Future<List<int>> reduce(
      List<int> Function(List<int> previous, List<int> element) combine) {
    return origin.reduce(combine);
  }

  @override
  Future<List<int>> get single => origin.single;

  @override
  Future<List<int>> singleWhere(bool Function(List<int> element) test,
      {List<int> Function()? orElse}) {
    return origin.singleWhere(test, orElse: orElse);
  }

  @override
  Stream<List<int>> skip(int count) {
    return origin.skip(count);
  }

  @override
  Stream<List<int>> skipWhile(bool Function(List<int> element) test) {
    return origin.skipWhile(test);
  }

  @override
  int get statusCode => origin.statusCode;

  @override
  Stream<List<int>> take(int count) {
    return origin.take(count);
  }

  @override
  Stream<List<int>> takeWhile(bool Function(List<int> element) test) {
    return origin.takeWhile(test);
  }

  @override
  Stream<List<int>> timeout(Duration timeLimit,
      {void Function(EventSink<List<int>> sink)? onTimeout}) {
    return origin.timeout(timeLimit, onTimeout: onTimeout);
  }

  @override
  Future<List<List<int>>> toList() {
    return origin.toList();
  }

  @override
  Future<Set<List<int>>> toSet() {
    return origin.toSet();
  }

  @override
  Stream<S> transform<S>(StreamTransformer<List<int>, S> streamTransformer) {
    Stream<S> s = origin.transform<S>(streamTransformer);
    if (!isTextResponse()) {
      recordResponse(
          statusCode, '返回结果不支持解析', headers.toString(), contentLength);
      return s;
    }
    s = s.asBroadcastStream();
    s.listen((S event) {
      if (event is Uint8List) {
        final Uint8List result = event;
        var encoding = getEncoding();
        if (encoding != null) {
          recordResponse(statusCode, encoding.decode(result.toList()),
              headers.toString(), event.length);
        } else {
          recordResponse(
              statusCode, '返回结果解析失败', headers.toString(), contentLength);
        }
      } else if (event is String) {
        recordResponse(statusCode, event, headers.toString(), contentLength);
      } else {
        recordResponse(statusCode, 'unknown type:${event.runtimeType}',
            headers.toString(), contentLength);
      }
    });
    return s;
  }

  @override
  Stream<List<int>> where(bool Function(List<int> event) test) {
    return origin.where(test);
  }
}
