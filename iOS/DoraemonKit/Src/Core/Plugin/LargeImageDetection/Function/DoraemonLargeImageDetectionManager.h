//
//  DoraemonLargeImageDetectionManager.h
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import <Foundation/Foundation.h>
@class DoraemonResponseImageModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonLargeImageDetectionManager : NSObject
@property (nonatomic, strong) NSMutableArray *images;
@property (nonatomic, assign) BOOL detecting;
@property (nonatomic, assign) int64_t minimumDetectionSize;

+ (instancetype) shareInstance;
- (void)updateInterceptStatus;
@end

NS_ASSUME_NONNULL_END
