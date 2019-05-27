//
//  DoraemonSubThreadUICheckManager.h
//  AFNetworking
//
//  Created by yixiang on 2018/9/13.
//

#import <Foundation/Foundation.h>

@interface DoraemonSubThreadUICheckManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, strong) NSMutableArray *checkArray;

@end
