//
//  DoraemonMockBaseModel.h
//  DoraemonKit
//
//  Created by didi on 2019/11/15.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMockBaseModel : NSObject

//通用业务数据
@property (nonatomic, copy) NSString *apiId; //接口id
@property (nonatomic, copy) NSString *name; //接口名称
@property (nonatomic, copy) NSString *path; //path
@property (nonatomic, copy) NSDictionary *query; //query
@property (nonatomic, copy) NSDictionary *body; //body
@property (nonatomic, copy) NSString *category; //分组
@property (nonatomic, copy) NSString *owner;//创建者
@property (nonatomic, copy) NSString *editor;//最新的一次修改人

//通用逻辑数据
@property (nonatomic, assign) BOOL expand;//该数据是否展开
@property (nonatomic, copy) NSString *info;//info面板数据，由path、query、category、owner、editor组成
@property (nonatomic, assign) BOOL selected;//是否选中
-  (void)appendFormat:(NSMutableString *)info text:(NSString *)text append:(NSString *)appendInfo;
@end

NS_ASSUME_NONNULL_END
