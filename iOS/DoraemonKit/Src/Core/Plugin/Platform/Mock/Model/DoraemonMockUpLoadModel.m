//
//  DoraemonMockUpLoadModel.m
//  AFNetworking
//
//  Created by didi on 2019/11/15.
//

#import "DoraemonMockUpLoadModel.h"

@implementation DoraemonMockUpLoadModel

- (NSString *)info{
    NSMutableString *info = [[NSMutableString alloc] init];
    if (self.path) {
        [info appendFormat:@"path: %@\n",self.path];
    }
    if (self.query && self.query.allKeys.count>0) {
        [info appendFormat:@"query: %@\n",self.query];
    }
    if (self.category) {
        [info appendFormat:@"分组: %@\n",self.category];
    }
    if (self.owner) {
        [info appendFormat:@"创建人: %@\n",self.owner];
    }
    if (self.editor) {
        [info appendFormat:@"修改人: %@\n",self.editor];
    }
    if (self.result && self.result.length>0) {
        [info appendString:@"本地是否存在mock数据: 存在"];
    }else{
        [info appendString:@"本地是否存在mock数据:  不存在"];
    }
    
    return info;
}

@end
