//
//  DoraemonMockUtil.h
//  DoraemonKit
//
//  Created by didi on 2019/11/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMockUtil : NSObject

+ (instancetype)sharedInstance;

- (void)saveMockArrayCache;

- (void)saveUploadArrayCache;

- (void)readMockArrayCache;

- (void)readUploadArrayCache;

@end

NS_ASSUME_NONNULL_END
