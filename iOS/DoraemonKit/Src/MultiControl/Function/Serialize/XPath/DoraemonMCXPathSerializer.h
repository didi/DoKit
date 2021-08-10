//
//  DoraemonMCXPath.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCXPathNode : NSObject

// 控件在父视图上的索引
@property (nonatomic , assign) NSUInteger index;
// 控件的类名
@property (nonatomic , copy) NSString *className;

+ (instancetype)nodeWithString:(NSString *)string;
 
@end

@interface DoraemonMCXPathSerializer : NSObject

/// 当前控件是否在复用模式容器的单元格 例如:UITableViewCell, UICollectionViewCell
@property (nonatomic , assign) BOOL isOneCell ;

/// 如果isOneCell为true , 这个字段有值 列表容器的XPath 例如 UITableView , UICollectionView
@property (nonatomic , strong , readonly) NSArray<DoraemonMCXPathNode *> *listContainerPathNodeList;

/// 如果isOneCell为true , 这个字段有值 代表当前控件所在cell在列表容器中的索引
@property (nonatomic , strong  ) NSIndexPath *cellIndexPath;

/// 从根window到当前控件的XPath 如果isOneCell为true , 就代表从listContainer到当前控件的XPath
@property (nonatomic , copy  , readonly) NSArray<DoraemonMCXPathNode *> *pathNodeList;

/// 控件所在window在当前所有window数组的索引
@property (nonatomic , assign) NSInteger windowIndex;

/// 控件所在window的类名
@property (nonatomic , copy  ) NSString *windowClsName;

/// 控件所在window的根控制器类名
@property (nonatomic , copy  ) NSString *windowRootVCClsName;

/// 默认false
@property (nonatomic , assign) BOOL ignore;

/// 控件所在控制器类名
@property (nonatomic , weak  ) UIViewController *vcCls;

/// 根据控件获取XPath描述对象
+ (instancetype )xPathInstanceWithView:(UIView *)view;

/// 解析网络上传过来的xpath字符串信息,生成xPath对象
+ (instancetype)xPathInstanceFromXpath:(NSString *)xpath;

/// 根据控件获取XPath在网络上传输的字符串
+ (NSString *)xPathStringWithView:(UIView *)view ;

/// 根据网络上传过来的xpath字符串还原uiview
+ (UIView *)viewFromXpath:(NSString *)xpath;

/// 根据当前信息生成xPath在网络上传输的字符串
- (NSString *)generalPathToTransfer;

/// 根据当前的xPath信息查找到对应的view
- (UIView *)fetchView;

// 获取view所在控制器
+ (UIViewController *)ownerVCWithView:(UIView *)view ;

@end

NS_ASSUME_NONNULL_END
