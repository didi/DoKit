//
//  DoraemonHealthManager.m
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import "DoraemonHealthManager.h"

@implementation DoraemonHealthManager

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        [self initItems];
    }
    return self;
}

- (void)initItems{
    _cellTitle = @[
        @"点击开始体检按钮开始本次的性能数据手机。",
        @"在每一个页面至少停留10秒钟，如果低于10秒钟的话，我们将会丢弃该页面的收集数据。",
        @"测试完毕之后，重新进入该页面，点击结束测试按钮，填写本次的测试用例名称和测试人的名字，即可上传。",
        @"打开dokit.cn平台，进入app健康体检列表，查看本次的数据报告。"
    ];
}

@end
