//
//  DoraemonHierarchyInfoView.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyInfoView.h"
#import "UIColor+DoraemonHierarchy.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"
#import "NSObject+DoraemonHierarchy.h"
#import "DoraemonHierarchyFormatterTool.h"

@interface DoraemonHierarchyInfoView ()

@property (nonatomic, strong, nullable) UIView *selectedView;

@property (nonatomic, strong) UIButton *closeButton;

@property (nonatomic, strong) UILabel *contentLabel;

@property (nonatomic, strong) UILabel *frameLabel;

@property (nonatomic, strong) UILabel *backgroundColorLabel;

@property (nonatomic, strong) UILabel *textColorLabel;

@property (nonatomic, strong) UILabel *fontLabel;

@property (nonatomic, strong) UILabel *tagLabel;

@property (nonatomic, strong) UIView *actionContentView;

@property (nonatomic, strong) UIButton *moreButton;

@property (nonatomic, strong) UIButton *parentViewsButton;

@property (nonatomic, strong) UIButton *subviewsButton;

@property (nonatomic, assign) CGFloat actionContentViewHeight;

@end

@implementation DoraemonHierarchyInfoView

- (void)updateSelectedView:(UIView *)selectedView {
    
    UIView *view = selectedView;
    
    if (!view) {
        return;
    }
    
    if (self.selectedView == view) {
        return;
    }
    
    self.moreButton.enabled = YES;
    self.parentViewsButton.enabled = view.superview != nil;
    self.subviewsButton.enabled = view.subviews.count;
    
    self.selectedView = view;
    
    NSDictionary *boldAttri = @{NSFontAttributeName: [UIFont boldSystemFontOfSize:17]};
    NSDictionary *attri = @{NSFontAttributeName: [UIFont systemFontOfSize:14]};
    
    NSMutableAttributedString *name = [[NSMutableAttributedString alloc] initWithString:@"Name: " attributes:boldAttri];
    [name appendAttributedString:[[NSAttributedString alloc] initWithString:NSStringFromClass(view.class) attributes:attri]];
    
    self.contentLabel.attributedText = name;
    
    NSMutableAttributedString *frame = [[NSMutableAttributedString alloc] initWithString:@"Frame: " attributes:boldAttri];
    [frame appendAttributedString:[[NSAttributedString alloc] initWithString:[DoraemonHierarchyFormatterTool stringFromFrame:view.frame] attributes:attri]];
    
    self.frameLabel.attributedText = frame;
    
    if (view.backgroundColor) {
        NSMutableAttributedString *color = [[NSMutableAttributedString alloc] initWithString:@"Background: " attributes:boldAttri];
        [color appendAttributedString:[[NSAttributedString alloc] initWithString:[view.backgroundColor doraemon_description] attributes:attri]];
        self.backgroundColorLabel.attributedText = color;
    } else {
        self.backgroundColorLabel.attributedText = nil;
    }
    
    if ([view isKindOfClass:[UILabel class]]) {
        UILabel *label = (UILabel *)view;
        NSMutableAttributedString *textColor = [[NSMutableAttributedString alloc] initWithString:@"Text Color: " attributes:boldAttri];
        [textColor appendAttributedString:[[NSAttributedString alloc] initWithString:[label.textColor doraemon_description] attributes:attri]];
        self.textColorLabel.attributedText = textColor;
        
        NSMutableAttributedString *font = [[NSMutableAttributedString alloc] initWithString:@"Font: " attributes:boldAttri];
        [font appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"%0.2f", label.font.pointSize] attributes:attri]];
        self.fontLabel.attributedText = font;
    } else {
        self.textColorLabel.attributedText = nil;
        self.fontLabel.attributedText = nil;
    }
    
    if (view.tag != 0) {
        NSMutableAttributedString *tag = [[NSMutableAttributedString alloc] initWithString:@"Tag: " attributes:boldAttri];
        [tag appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"%ld",(long)view.tag] attributes:attri]];
        self.tagLabel.attributedText = tag;
    } else {
        self.tagLabel.attributedText = nil;
    }
    
    [self.contentLabel sizeToFit];
    [self.frameLabel sizeToFit];
    [self.backgroundColorLabel sizeToFit];
    [self.textColorLabel sizeToFit];
    [self.fontLabel sizeToFit];
    [self.tagLabel sizeToFit];
    
    [self updateHeightIfNeeded];
}

- (void)layoutSubviews {
    [super layoutSubviews];

    self.closeButton.frame = CGRectMake(self.doraemon_width - 10 - 30, 10, 30, 30);
    
    self.actionContentView.frame = CGRectMake(0, self.doraemon_height - self.actionContentViewHeight - 10, self.doraemon_width, self.actionContentViewHeight);
    
    self.parentViewsButton.frame = CGRectMake(10, 0, self.actionContentView.doraemon_width / 2.0 - 10 * 1.5, (self.actionContentView.doraemon_height - 10) / 2.0);
    
    self.subviewsButton.frame = CGRectMake(self.actionContentView.doraemon_width / 2.0 + 10 * 0.5, self.parentViewsButton.doraemon_top, self.parentViewsButton.doraemon_width, self.parentViewsButton.doraemon_height);
    
    self.moreButton.frame = CGRectMake(10, self.parentViewsButton.doraemon_bottom + 10, self.actionContentView.doraemon_width - 10 * 2, self.parentViewsButton.doraemon_height);
    
    self.contentLabel.frame = CGRectMake(10, 10, self.closeButton.doraemon_x - 10 - 10, self.contentLabel.doraemon_height);
    
    self.frameLabel.frame = CGRectMake(self.contentLabel.doraemon_x, self.contentLabel.doraemon_bottom, self.contentLabel.doraemon_width, self.frameLabel.doraemon_height);
    
    self.backgroundColorLabel.frame = CGRectMake(self.contentLabel.doraemon_x, self.frameLabel.doraemon_bottom, self.contentLabel.doraemon_width, self.backgroundColorLabel.doraemon_height);
    
    self.textColorLabel.frame = CGRectMake(self.contentLabel.doraemon_x, self.backgroundColorLabel.doraemon_bottom, self.contentLabel.doraemon_width, self.textColorLabel.doraemon_height);
    
    self.fontLabel.frame = CGRectMake(self.contentLabel.doraemon_x, self.textColorLabel.doraemon_bottom, self.contentLabel.doraemon_width, self.fontLabel.doraemon_height);
    
    self.tagLabel.frame = CGRectMake(self.contentLabel.doraemon_x, self.fontLabel.doraemon_bottom, self.contentLabel.doraemon_width, self.tagLabel.doraemon_height);
}

#pragma mark - Over write
- (void)initUI {
    [super initUI];
    
    self.layer.borderColor = [UIColor doraemon_black_1].CGColor;
    self.layer.borderWidth = 2;
    self.layer.cornerRadius = 5;
    self.layer.masksToBounds = YES;
    self.backgroundColor = [UIColor whiteColor];
    
    self.actionContentViewHeight = 80;
    
    [self addSubview:self.closeButton];
    [self addSubview:self.contentLabel];
    [self addSubview:self.frameLabel];
    [self addSubview:self.backgroundColorLabel];
    [self addSubview:self.textColorLabel];
    [self addSubview:self.fontLabel];
    [self addSubview:self.tagLabel];
    [self addSubview:self.actionContentView];
    [self.actionContentView addSubview:self.parentViewsButton];
    [self.actionContentView addSubview:self.subviewsButton];
    [self.actionContentView addSubview:self.moreButton];
    
    [self updateHeightIfNeeded];
}

#pragma mark - Event responses
- (void)buttonClicked:(UIButton *)sender {
    [self.delegate doraemonHierarchyInfoView:self didSelectAt:sender.tag];
}

- (void)closeButtonClicked:(UIButton *)sender {
    [self.delegate doraemonHierarchyInfoViewDidSelectCloseButton:self];
}

- (void)frameLabelTapGestureRecognizer:(UITapGestureRecognizer *)sender {
    [self.selectedView doraemon_showFrameAlertAndAutomicSetWithKeyPath:@"frame"];
}

- (void)backgroundColorLabelTapGestureRecognizer:(UITapGestureRecognizer *)sender {
    [self.selectedView doraemon_showColorAlertAndAutomicSetWithKeyPath:@"backgroundColor"];
}

- (void)textColorLabelTapGestureRecognizer:(UITapGestureRecognizer *)sender {
    [self.selectedView doraemon_showColorAlertAndAutomicSetWithKeyPath:@"textColor"];
}

- (void)fontLabelTapGestureRecognizer:(UITapGestureRecognizer *)sender {
    [self.selectedView doraemon_showFontAlertAndAutomicSetWithKeyPath:@"font"];
}

- (void)tagLabelTapGestureRecognizer:(UITapGestureRecognizer *)sender {
    [self.selectedView doraemon_showIntAlertAndAutomicSetWithKeyPath:@"tag"];
}

#pragma mark - Primary
- (void)updateHeightIfNeeded {
    CGFloat contentHeight = self.contentLabel.doraemon_height + self.frameLabel.doraemon_height + self.backgroundColorLabel.doraemon_height + self.textColorLabel.doraemon_height + self.fontLabel.doraemon_height + self.tagLabel.doraemon_height;
    CGFloat height = 10 + MAX(contentHeight, 10 + 30/*self.closeButton.doraemon_height*/) + 10 + self.actionContentViewHeight + 10;
    if (height != self.doraemon_height) {
        self.doraemon_height = height;
        if (!self.isMoved) {
            if (self.doraemon_bottom != DoraemonScreenHeight - 10 * 2) {
                self.doraemon_bottom = DoraemonScreenHeight - 10 * 2;
            }
        }
    }
}

#pragma mark - Getters and setters
- (UIButton *)closeButton {
    if (!_closeButton) {
        _closeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_closeButton addTarget:self action:@selector(closeButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_closeButton setImage:[UIImage doraemon_imageNamed:@"doraemon_close"] forState:UIControlStateNormal];
    }
    return _closeButton;
}

- (UILabel *)contentLabel {
    if (!_contentLabel) {
        _contentLabel = [[UILabel alloc] init];
        _contentLabel.font = [UIFont systemFontOfSize:14];
        _contentLabel.textColor = [UIColor doraemon_black_1];
        _contentLabel.numberOfLines = 0;
        _contentLabel.lineBreakMode = NSLineBreakByCharWrapping;
    }
    return _contentLabel;
}

- (UILabel *)frameLabel {
    if (!_frameLabel) {
        _frameLabel = [[UILabel alloc] init];
        _frameLabel.font = [UIFont systemFontOfSize:14];
        _frameLabel.textColor = [UIColor doraemon_black_1];
        _frameLabel.numberOfLines = 0;
        _frameLabel.lineBreakMode = NSLineBreakByCharWrapping;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(frameLabelTapGestureRecognizer:)];
        _frameLabel.userInteractionEnabled = YES;
        [_frameLabel addGestureRecognizer:tap];
    }
    return _frameLabel;
}

- (UILabel *)backgroundColorLabel {
    if (!_backgroundColorLabel) {
        _backgroundColorLabel = [[UILabel alloc] init];
        _backgroundColorLabel.font = [UIFont systemFontOfSize:14];
        _backgroundColorLabel.textColor = [UIColor doraemon_black_1];
        _backgroundColorLabel.numberOfLines = 0;
        _backgroundColorLabel.lineBreakMode = NSLineBreakByCharWrapping;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(backgroundColorLabelTapGestureRecognizer:)];
        _backgroundColorLabel.userInteractionEnabled = YES;
        [_backgroundColorLabel addGestureRecognizer:tap];
    }
    return _backgroundColorLabel;
}

- (UILabel *)textColorLabel {
    if (!_textColorLabel) {
        _textColorLabel = [[UILabel alloc] init];
        _textColorLabel.font = [UIFont systemFontOfSize:14];
        _textColorLabel.textColor = [UIColor doraemon_black_1];
        _textColorLabel.numberOfLines = 0;
        _textColorLabel.lineBreakMode = NSLineBreakByCharWrapping;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(textColorLabelTapGestureRecognizer:)];
        _textColorLabel.userInteractionEnabled = YES;
        [_textColorLabel addGestureRecognizer:tap];
    }
    return _textColorLabel;
}

- (UILabel *)fontLabel {
    if (!_fontLabel) {
        _fontLabel = [[UILabel alloc] init];
        _fontLabel.font = [UIFont systemFontOfSize:14];
        _fontLabel.textColor = [UIColor doraemon_black_1];
        _fontLabel.numberOfLines = 0;
        _fontLabel.lineBreakMode = NSLineBreakByCharWrapping;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(fontLabelTapGestureRecognizer:)];
        _fontLabel.userInteractionEnabled = YES;
        [_fontLabel addGestureRecognizer:tap];
    }
    return _fontLabel;
}

- (UILabel *)tagLabel {
    if (!_tagLabel) {
        _tagLabel = [[UILabel alloc] init];
        _tagLabel.font = [UIFont systemFontOfSize:14];
        _tagLabel.textColor = [UIColor doraemon_black_1];
        _tagLabel.numberOfLines = 0;
        _tagLabel.lineBreakMode = NSLineBreakByCharWrapping;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tagLabelTapGestureRecognizer:)];
        _tagLabel.userInteractionEnabled = YES;
        [_tagLabel addGestureRecognizer:tap];
    }
    return _tagLabel;
}

- (UIView *)actionContentView {
    if (!_actionContentView) {
        _actionContentView = [[UIView alloc] init];
    }
    return _actionContentView;
}

- (UIButton *)parentViewsButton {
    if (!_parentViewsButton) {
        _parentViewsButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_parentViewsButton addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_parentViewsButton setTitle:@"Parent Views" forState:UIControlStateNormal];
        [_parentViewsButton setTitleColor:[UIColor doraemon_black_1] forState:UIControlStateNormal];
        _parentViewsButton.titleLabel.font = [UIFont systemFontOfSize:14];
        _parentViewsButton.backgroundColor = [UIColor whiteColor];
        _parentViewsButton.layer.borderColor = [UIColor doraemon_black_1].CGColor;
        _parentViewsButton.layer.borderWidth = 1;
        _parentViewsButton.layer.cornerRadius = 5;
        _parentViewsButton.layer.masksToBounds = YES;
        _parentViewsButton.tintColor = [UIColor doraemon_black_1];
        _parentViewsButton.imageEdgeInsets = UIEdgeInsetsMake(0, 0, 0, 10);
        [_parentViewsButton setImage:[[UIImage doraemon_imageNamed:@"doraemon_hierarchy_parent"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate] forState:UIControlStateNormal];
        _parentViewsButton.tag = DoraemonHierarchyInfoViewActionShowParent;
        _parentViewsButton.enabled = NO;
    }
    return _parentViewsButton;
}

- (UIButton *)subviewsButton {
    if (!_subviewsButton) {
        _subviewsButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_subviewsButton addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_subviewsButton setTitle:@"Subviews" forState:UIControlStateNormal];
        [_subviewsButton setTitleColor:[UIColor doraemon_black_1] forState:UIControlStateNormal];
        _subviewsButton.titleLabel.font = [UIFont systemFontOfSize:14];
        _subviewsButton.backgroundColor = [UIColor whiteColor];
        _subviewsButton.layer.borderColor = [UIColor doraemon_black_1].CGColor;
        _subviewsButton.layer.borderWidth = 1;
        _subviewsButton.layer.cornerRadius = 5;
        _subviewsButton.layer.masksToBounds = YES;
        _subviewsButton.tintColor = [UIColor doraemon_black_1];
        _subviewsButton.imageEdgeInsets = UIEdgeInsetsMake(0, 0, 0, 10);
        [_subviewsButton setImage:[[UIImage doraemon_imageNamed:@"doraemon_hierarchy_subview"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate] forState:UIControlStateNormal];
        _subviewsButton.tag = DoraemonHierarchyInfoViewActionShowSubview;
        _subviewsButton.enabled = NO;
    }
    return _subviewsButton;
}

- (UIButton *)moreButton {
    if (!_moreButton) {
        _moreButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_moreButton addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_moreButton setTitle:@"More Info" forState:UIControlStateNormal];
        [_moreButton setTitleColor:[UIColor doraemon_black_1] forState:UIControlStateNormal];
        _moreButton.titleLabel.font = [UIFont systemFontOfSize:14];
        _moreButton.backgroundColor = [UIColor whiteColor];
        _moreButton.layer.borderColor = [UIColor doraemon_black_1].CGColor;
        _moreButton.layer.borderWidth = 1;
        _moreButton.layer.cornerRadius = 5;
        _moreButton.layer.masksToBounds = YES;
        _moreButton.tintColor = [UIColor doraemon_black_1];
        _moreButton.imageEdgeInsets = UIEdgeInsetsMake(0, 0, 0, 10);
        [_moreButton setImage:[[UIImage doraemon_imageNamed:@"doraemon_hierarchy_info"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate] forState:UIControlStateNormal];
        _moreButton.tag = DoraemonHierarchyInfoViewActionShowMoreInfo;
        _moreButton.enabled = NO;
    }
    return _moreButton;
}

@end
