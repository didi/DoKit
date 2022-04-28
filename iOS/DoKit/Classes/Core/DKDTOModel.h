//
//  DKDTOModel.h
//  DoraemonKit
//
//  Created by 唐佳诚 on 2022/4/28.
//

#import <Mantle/Mantle.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKDTOModel : MTLModel <MTLJSONSerializing>

@property(nonatomic, nullable, copy) NSString *requestId;

@property(nonatomic, nullable, copy) NSString *method;

@property(nonatomic, nullable, copy) NSString *data;

@end

NS_ASSUME_NONNULL_END
