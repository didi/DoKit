//
//  DoraemonLargeImageDetectionListViewController.h
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import <UIKit/UIKit.h>
#import "DoraemonBaseViewController.h"
@class DoraemonResponseImageModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonLargeImageDetectionListViewController : DoraemonBaseViewController
- (instancetype)initWithImages:(NSArray <DoraemonResponseImageModel *> *) images;
@end

NS_ASSUME_NONNULL_END
