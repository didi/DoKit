class LeaksDoctorConf {
  int? maxRetainingPathLimit;

  // 策略池 存储期望类对象实例个数
  final Map<String, int> _policyCachePool = {};

  // 查询设置的策略
  int? searchPolicy(String clsName) => _policyCachePool[clsName];

  // 存储策略
  void savePolicy(String clsName, int expectedTotalCount) =>
      _policyCachePool[clsName] = expectedTotalCount;

 
  static final LeaksDoctorConf _instance = LeaksDoctorConf._internal();
  factory LeaksDoctorConf(){
    return _instance;
  }

  LeaksDoctorConf._internal(){
    init();
  }
  void init(){
    maxRetainingPathLimit=200;
  }

}