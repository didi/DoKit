//
//  DoraemonWeakNetworkHandle.h
//  DoraemonKit
//
//  Created by didi on 2019/12/12.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonWeakNetworkHandle : NSObject

- (NSData *)weakFlow:(NSData *)data count:(NSInteger)times size:(NSInteger)weakSize;

@end

NS_ASSUME_NONNULL_END
