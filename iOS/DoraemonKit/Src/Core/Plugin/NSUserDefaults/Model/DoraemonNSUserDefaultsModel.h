//
//  DoraemonNSUserDefaultsModel.h
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/11/26.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonNSUserDefaultsModel : NSObject
@property (nonatomic, copy) NSString *key;
@property (nonatomic, strong) id value;
@end

NS_ASSUME_NONNULL_END
