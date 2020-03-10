//
//  DoraemonMockUploadListView.h
//  AFNetworking
//
//  Created by didi on 2019/11/15.
//

#import "DoraemonMockBaseListView.h"
#import "DoraemonMockUpLoadModel.h"

NS_ASSUME_NONNULL_BEGIN
@protocol DoraemonMockUploadListViewDelegate <NSObject>

- (void)previewClick:(DoraemonMockUpLoadModel *)uploadModel;

@end

@interface DoraemonMockUploadListView : DoraemonMockBaseListView

@property (nonatomic, weak) id<DoraemonMockUploadListViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
