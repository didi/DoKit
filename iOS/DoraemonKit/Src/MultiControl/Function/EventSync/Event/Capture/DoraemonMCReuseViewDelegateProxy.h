//
//  DoraemonMCReuseViewDelegateProxy.h
//  DoraemonKit
//
//  Created by litianhao on 2021/7/16.
//

#import <UIKit/UIKit.h>


@interface DoraemonMCReuseViewDelegateProxy : NSProxy <UICollectionViewDelegate , UITableViewDelegate>

@property (nonatomic , weak) id target;

+ (instancetype)proxyWithTarget:(id)target;

@end


