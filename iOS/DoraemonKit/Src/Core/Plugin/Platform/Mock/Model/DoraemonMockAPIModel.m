//
//  DoraemonMockAPI.m
//  DoraemonKit
//
//  Created by didi on 2019/11/12.
//

#import "DoraemonMockAPIModel.h"

@implementation DoraemonMockAPIModel

- (NSString *)info{
    NSMutableString *info = [[NSMutableString alloc] init];
    if (self.path) {
        [self appendFormat:info text:@"path : %@\n" append:self.path];
    }
    if (self.query && self.query.allKeys.count>0) {
        [info appendFormat :@"query: %@\n",self.query];
    }
    if (self.body && self.body.allValues.count>0) {
        [info appendFormat :@"body: %@\n",self.body];
    }
    if (self.category) {
        [self appendFormat:info text:@"分组 : %@\n" append:self.category];
    }
    if (self.owner) {
        [self appendFormat:info text:@"创建人 : %@\n" append:self.owner];
    }
    if (self.editor) {
        [self appendFormat:info text:@"修改人 : %@\n" append:self.editor];
    }
    
    [info replaceCharactersInRange:NSMakeRange([info length] - 1, 1) withString:@""];
    
    return info;
}

@end
