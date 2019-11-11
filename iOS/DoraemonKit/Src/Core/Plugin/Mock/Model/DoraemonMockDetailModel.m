//
//  DoraemonMockDetailModel.m
//  AFNetworking
//
//  Created by didi on 2019/10/31.
//

#import "DoraemonMockDetailModel.h"

@interface DoraemonMockDetailModel()

@property (nonatomic, strong) DoraemonMockDetailModel *model;

@end

@implementation DoraemonMockDetailModel

- (instancetype)init{
    self = [super init];
    if(self){
        _content = [NSMutableDictionary dictionary];
    }
    return self;
}

- (DoraemonMockDetailModel *)packageArrayToModel:(NSArray *)info{
    _content[@"xx"] = @"Dsad";//info[0]
    _content[@"xxxx"] = @"Dsadxxx";//info[1]
    
    return self;
}

@end
