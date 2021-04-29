import 'dart:async';
import 'package:isolate/isolate.dart';

class IsoPool {
  static final IsoPool _instance = IsoPool._internal();

  factory IsoPool() {
    return _instance;
  }

  IsoPool._internal();

  LoadBalancer _loadBalance;
  bool _isBalanceRunning = false;

  Future<R> start<R, P>(FutureOr<R> Function(P argument) function, argument) {
    return _isBalanceRunning
        ? _calculateMore(function, argument)
        : _startBalance(function, argument);
  }

  Future<R> _startBalance<R, P>(
      FutureOr<R> Function(P argument) function, argument) async {
    if (_isBalanceRunning)
      throw Exception('ISO pool is running, do not start again');
    _loadBalance = await LoadBalancer.create(1, IsolateRunner.spawn);
    final res = await _loadBalance.run<R, P>(function, argument);
    _isBalanceRunning = true;
    return res;
  }

  Future<R> _calculateMore<R, P>(
      FutureOr<R> Function(P argument) function, argument) async {
    if (!_isBalanceRunning) throw Exception('ISO pool is not running');
    return await _loadBalance.run<R, P>(function, argument);
  }
}
