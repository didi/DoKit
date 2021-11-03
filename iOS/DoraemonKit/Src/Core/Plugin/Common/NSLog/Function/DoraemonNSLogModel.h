//
//  DoraemonNSLogModel.h
//  DoraemonKit
//
//  Created by yixiang on 2018/11/26.
//

#import <Foundation/Foundation.h>

@interface DoraemonNSLogModel : NSObject

@property (nonatomic, copy) NSString *content;
@property (nonatomic, assign) NSTimeInterval timeInterval;
@property (nonatomic, assign) BOOL expand;

@end
