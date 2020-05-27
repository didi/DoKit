//
//  DoraemonPageTimeInstance.h
//  DoraemonKit
//
//  Created by Frank on 2020/5/27.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonPageTimeInstance : NSObject

+ (instancetype)sharedInstance;

- (NSArray *)getArrayRecord;

- (void)timeWithVC:(id)vc sel:(SEL)sel;

@end

NS_ASSUME_NONNULL_END
