//
//  NSArray+JSON.m
//  DoraemonKit
//
//  Created by wzp on 2021/10/11.
//

#import "NSArray+JSON.h"
#import <JSONModel/JSONModel.h>

@implementation NSArray (JSON)

+ (NSArray *)arrayWithJsonArray:(NSArray<NSDictionary *> *)aJsonArray modelClassName:(NSString *) aModelClassName{
    
    NSMutableArray *objArray =  [[NSMutableArray alloc]init];
    for (NSDictionary *jsonDic in aJsonArray) {
        if (jsonDic && [jsonDic isKindOfClass:[NSDictionary class]]) {
            NSError *err = nil;
            Class cls = NSClassFromString(aModelClassName);
            JSONModel *model = [[cls alloc]initWithDictionary:jsonDic error:&err];
            if (!err && model) {
                [objArray addObject:model];
            }
        }
    }
    return objArray.count > 0 ? objArray : nil;
}


@end
