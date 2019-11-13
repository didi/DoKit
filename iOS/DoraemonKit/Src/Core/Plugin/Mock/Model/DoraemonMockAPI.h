//
//  DoraemonMockAPI.h
//  AFNetworking
//
//  Created by didi on 2019/11/12.
//

#import <Foundation/Foundation.h>
#import "DoraemonMockScene.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMockAPI : NSObject

//业务数据
@property (nonatomic, copy) NSString *apiId; //接口id
@property (nonatomic, copy) NSString *name; //接口名称
@property (nonatomic, copy) NSString *path; //path
@property (nonatomic, copy) NSDictionary *query; //query
@property (nonatomic, copy) NSString *category; //分组
@property (nonatomic, copy) NSString *owner;//创建者
@property (nonatomic, copy) NSString *editor;//最新的一次修改人
@property (nonatomic, copy) NSArray<DoraemonMockScene *> *sceneList;

//逻辑数据
@property (nonatomic, assign) BOOL expand;//该数据是否展开
@property (nonatomic, copy) NSString *info;//info面板数据，由path、query、category、owner、editor组成

@end

NS_ASSUME_NONNULL_END
