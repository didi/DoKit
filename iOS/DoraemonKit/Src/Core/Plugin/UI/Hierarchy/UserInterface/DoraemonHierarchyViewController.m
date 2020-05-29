//
//  DoraemonHierarchyViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyViewController.h"
#import "DoraemonHierarchyDetailViewController.h"
#import "UIViewController+DoraemonHierarchy.h"
#import "DoraemonHierarchyPickerView.h"
#import "NSObject+DoraemonHierarchy.h"
#import "DoraemonHierarchyInfoView.h"
#import "DoraemonHierarchyHelper.h"
#import "DoraemonHierarchyWindow.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"

@interface DoraemonHierarchyViewController ()<DoraemonHierarchyViewDelegate, DoraemonHierarchyInfoViewDelegate>

@property (nonatomic, strong) UIView *borderView;

@property (nonatomic, strong) DoraemonHierarchyPickerView *pickerView;

@property (nonatomic, strong) DoraemonHierarchyInfoView *infoView;

@property (nonatomic, strong) NSMutableSet *observeViews;

@property (nonatomic, strong) NSMutableDictionary *borderViews;

@end

@implementation DoraemonHierarchyViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.1];
    self.observeViews = [NSMutableSet set];
    self.borderViews = [[NSMutableDictionary alloc] init];
    
    CGFloat height = 100;
    self.infoView = [[DoraemonHierarchyInfoView alloc] initWithFrame:CGRectMake(10, DoraemonScreenHeight - 10 * 2 - height, DoraemonScreenWidth - 10 * 2, height)];
    self.infoView.delegate = self;
    [self.view addSubview:self.infoView];
    
    [self.view addSubview:self.borderView];
    
    self.pickerView = [[DoraemonHierarchyPickerView alloc] initWithFrame:CGRectMake((self.view.doraemon_width - 60) / 2.0, (self.view.doraemon_height - 60) / 2.0, 60, 60)];
    self.pickerView.delegate = self;
    [self.view addSubview:self.pickerView];
}

- (void)dealloc {
    for (UIView *view in self.observeViews) {
        [self stopObserveView:view];
    }
    [self.observeViews removeAllObjects];
}

#pragma mark - Primary
- (void)beginObserveView:(UIView *)view borderWidth:(CGFloat)borderWidth {
    if ([self.observeViews containsObject:view]) {
        return;
    }
    
    UIView *borderView = [[UIView alloc] init];
    borderView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:borderView];
    [self.view sendSubviewToBack:borderView];
    borderView.layer.borderColor = view.doraemon_hashColor.CGColor;
    borderView.layer.borderWidth = borderWidth;
    borderView.frame = [self frameInLocalForView:view];
    [self.borderViews setObject:borderView forKey:@(view.hash)];

    [view addObserver:self forKeyPath:@"frame" options:0 context:NULL];
}

- (void)stopObserveView:(UIView *)view {
    if (![self.observeViews containsObject:view]) {
        return;
    }
    
    UIView *borderView = self.borderViews[@(view.hash)];
    [borderView removeFromSuperview];
    [view removeObserver:self forKeyPath:@"frame"];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSString *, id> *)change context:(void *)context {
    if ([object isKindOfClass:[UIView class]]) {
        UIView *view = (UIView *)object;
        [self updateOverlayIfNeeded:view];
    }
}

- (void)updateOverlayIfNeeded:(UIView *)view {
    UIView *borderView = self.borderViews[@(view.hash)];
    if (borderView) {
        borderView.frame = [self frameInLocalForView:view];
    }
}

- (CGRect)frameInLocalForView:(UIView *)view {
    UIWindow *window = [DoraemonUtil getKeyWindow];
    CGRect rect = [view convertRect:view.bounds toView:window];
    rect = [self.view convertRect:rect fromView:window];
    return rect;
}

- (UIView *)findSelectedViewInViews:(NSArray *)selectedViews {
    if ([DoraemonHierarchyHelper shared].isHierarchyIgnorePrivateClass) {
        NSMutableArray *views = [[NSMutableArray alloc] init];
        for (UIView *view in selectedViews) {
            if (![NSStringFromClass(view.class) hasPrefix:@"_"]) {
                [views addObject:view];
            }
        }
        return [views lastObject];
    } else {
        return [selectedViews lastObject];
    }
}

- (NSArray <UIView *>*)findParentViewsBySelectedView:(UIView *)selectedView {
    NSMutableArray *views = [[NSMutableArray alloc] init];
    UIView *view = [selectedView superview];
    while (view) {
        if ([DoraemonHierarchyHelper shared].isHierarchyIgnorePrivateClass) {
            if (![NSStringFromClass(view.class) hasPrefix:@"_"]) {
                [views addObject:view];
            }
        } else {
            [views addObject:view];
        }
        view = view.superview;
    }
    return [views copy];
}

- (NSArray <UIView *>*)findSubviewsBySelectedView:(UIView *)selectedView {
    NSMutableArray *views = [[NSMutableArray alloc] init];
    for (UIView *view in selectedView.subviews) {
        if ([DoraemonHierarchyHelper shared].isHierarchyIgnorePrivateClass) {
            if (![NSStringFromClass(view.class) hasPrefix:@"_"]) {
                [views addObject:view];
            }
        } else {
            [views addObject:view];
        }
    }
    return [views copy];
}

#pragma mark - LLHierarchyPickerViewDelegate
- (void)doraemonHierarchyView:(DoraemonHierarchyPickerView *)view didMoveTo:(NSArray <UIView *>*)selectedViews {
    
    @synchronized (self) {
        for (UIView *view in self.observeViews) {
            [self stopObserveView:view];
        }
        [self.observeViews removeAllObjects];
        
        for (NSInteger i = selectedViews.count - 1; i >= 0; i--) {
            UIView *view = selectedViews[i];
            CGFloat borderWidth = 1;
            if (i == selectedViews.count - 1) {
                borderWidth = 2;
            }
            [self beginObserveView:view borderWidth:borderWidth];
        }
        [self.observeViews addObjectsFromArray:selectedViews];
    }

    [self.infoView updateSelectedView:[self findSelectedViewInViews:selectedViews]];
}

#pragma mark - DoraemonHierarchyInfoViewDelegate
- (void)doraemonHierarchyInfoView:(DoraemonHierarchyInfoView *)view didSelectAt:(DoraemonHierarchyInfoViewAction)action {
    UIView *selectView = self.infoView.selectedView;
    if (selectView == nil) {
        return;
    }
    switch (action) {
        case DoraemonHierarchyInfoViewActionShowMoreInfo:{
            [self showHierarchyInfo:selectView];
        }
            break;
        case DoraemonHierarchyInfoViewActionShowParent: {
            [self showParentSheet:selectView];
        }
            break;
        case DoraemonHierarchyInfoViewActionShowSubview: {
            [self showSubviewSheet:selectView];
        }
            break;
    }
}

- (void)doraemonHierarchyInfoViewDidSelectCloseButton:(DoraemonHierarchyInfoView *)view {
    [[DoraemonHierarchyHelper shared].window hide];
    [DoraemonHierarchyHelper shared].window = nil;
}

- (void)showHierarchyInfo:(UIView *)selectView {
    DoraemonHierarchyDetailViewController *vc = [[DoraemonHierarchyDetailViewController alloc] init];
    vc.modalPresentationStyle = UIModalPresentationFullScreen;
    vc.selectView = selectView;
    [self presentViewController:vc animated:YES completion:nil];
}

- (void)showParentSheet:(UIView *)selectView {
    NSMutableArray *actions = [[NSMutableArray alloc] init];
    __block NSArray *parentViews = [self findParentViewsBySelectedView:selectView];
    for (UIView *view in parentViews) {
        [actions addObject:NSStringFromClass(view.class)];
    }
    __weak typeof(self) weakSelf = self;
    [self doraemon_showActionSheetWithTitle:@"Parent Views" actions:actions currentAction:nil completion:^(NSInteger index) {
        [weakSelf setNewSelectView:parentViews[index]];
    }];
}

- (void)showSubviewSheet:(UIView *)selectView {
    NSMutableArray *actions = [[NSMutableArray alloc] init];
    __block NSArray *subviews = [self findSubviewsBySelectedView:selectView];
    for (UIView *view in subviews) {
        [actions addObject:NSStringFromClass(view.class)];
    }
    __weak typeof(self) weakSelf = self;
    [self doraemon_showActionSheetWithTitle:@"Subviews" actions:actions currentAction:nil completion:^(NSInteger index) {
        [weakSelf setNewSelectView:subviews[index]];
    }];
}

- (void)setNewSelectView:(UIView *)view {
    [self doraemonHierarchyView:self.pickerView didMoveTo:@[view]];
}

#pragma mark - Getters and setters
- (UIView *)borderView {
    if (!_borderView) {
        _borderView = [[UIView alloc] init];
        _borderView.backgroundColor = [UIColor clearColor];
        _borderView.layer.borderWidth = 2;
    }
    return _borderView;
}

@end
