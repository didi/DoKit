//
//  DoraemonMockBaseModel.m
//  AFNetworking
//
//  Created by didi on 2019/11/15.
//

#import "DoraemonMockBaseModel.h"
#import "DoraemonDefine.h"

@implementation DoraemonMockBaseModel

-  (void)appendFormat:(NSMutableString *)info text:(NSString *)text append:(NSString *)appendInfo{
    [info appendFormat:DoraemonLocalizedString(text),appendInfo];
}

@end
