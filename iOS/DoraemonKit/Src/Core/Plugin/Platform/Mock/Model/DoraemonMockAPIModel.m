//
//  DoraemonMockAPI.m
//  AFNetworking
//
//  Created by didi on 2019/11/12.
//

#import "DoraemonMockAPIModel.h"

@implementation DoraemonMockAPIModel

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
    
    [info replaceCharactersInRange:NSMakeRange([info length] - 1, 1) withString:@""];
    
    return info;
}

@end
