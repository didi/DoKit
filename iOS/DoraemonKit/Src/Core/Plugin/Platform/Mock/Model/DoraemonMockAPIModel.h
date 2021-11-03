//
//  DoraemonMockAPI.h
//  DoraemonKit
//
//  Created by didi on 2019/11/12.
//

#import <Foundation/Foundation.h>
#import "DoraemonMockBaseModel.h"
#import "DoraemonMockScene.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMockAPIModel : DoraemonMockBaseModel

//业务数据
@property (nonatomic, copy) NSArray<DoraemonMockScene *> *sceneList;

@end

NS_ASSUME_NONNULL_END
