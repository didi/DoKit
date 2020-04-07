//
//  DoraemonResponseImageModel.h
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonResponseImageModel : NSObject
@property (nonatomic, strong) NSURL *url;
@property (nonatomic, strong) NSData *data;
@property (nonatomic, copy) NSString *size;

- (instancetype)initWithResponse: (NSURLResponse *)response data:(NSData *) data;
@end

NS_ASSUME_NONNULL_END
