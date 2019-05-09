//
//  DoraemonSanboxDetailViewController.m
//  AFNetworking
//
//  Created by yixiang on 2018/6/20.
//

#import <AVKit/AVKit.h>
#import <AVFoundation/AVFoundation.h>
#import "DoraemonSanboxDetailViewController.h"
#import "DoraemonToastUtil.h"
#import "UIView+Doraemon.h"
#import "Doraemoni18NUtil.h"

@interface DoraemonSanboxDetailViewController ()

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UITextView *textView;
@property (nonatomic, strong) AVPlayerViewController *playerView;

@end

@implementation DoraemonSanboxDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"文件预览");
    
    if (self.filePath.length>0) {
        NSString *path = self.filePath;
        if ([path hasSuffix:@".png"]  || [path hasSuffix:@".PNG"]
            || [path hasSuffix:@".jpg"]  || [path hasSuffix:@".JPG"]
            || [path hasSuffix:@".jpeg"] || [path hasSuffix:@".JPEG"]
            || [path hasSuffix:@".gif"]  || [path hasSuffix:@".GIF"]) {
            // 图片文件
            UIImage *img = [[UIImage alloc]initWithContentsOfFile:self.filePath];
            [self setOriginalImage:img];
            
        } else if ([path hasSuffix:@".strings"] || [path hasSuffix:@".plist"]
                   || [path hasSuffix:@".txt"] || [path hasSuffix:@".log"]
                   || [path hasSuffix:@".csv"]) {
            // 文本文件
            if ( [path hasSuffix:@".plist"] || [path hasSuffix:@".strings"] ){
                [self setContent:[[NSDictionary dictionaryWithContentsOfFile:path] description]];
            } else {
                NSData * data = [NSData dataWithContentsOfFile:path];
                [self setContent:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
            }
            
        } else if ([path hasSuffix:@".mp4"] || [path hasSuffix:@".MP4"]
                   || [path hasSuffix:@".mov"] || [path hasSuffix:@".MOV"]
                   || [path hasSuffix:@".mp3"] || [path hasSuffix:@".MP3"]) {
            // 音视频文件
            [self setMediaFile:self.filePath];
            
        } else {
            // 其他文件
            NSString *str = [NSString stringWithFormat:@"Not support %@ file!", [path pathExtension]];
            [self setContent:str];
        }
    }else{
        [DoraemonToastUtil showToast:DoraemonLocalizedString(@"文件不存在")];
    }
}

- (void)setContent:(NSString *)text {
    _textView = [[UITextView alloc] init];
    _textView.font = [UIFont systemFontOfSize:12.0f];
    _textView.textColor = [UIColor blackColor];
    _textView.textAlignment = NSTextAlignmentLeft;
    _textView.editable = NO;
    _textView.dataDetectorTypes = UIDataDetectorTypeLink;
    _textView.scrollEnabled = YES;
    _textView.backgroundColor = [UIColor whiteColor];
    _textView.layer.borderColor = [UIColor grayColor].CGColor;
    _textView.layer.borderWidth = 2.0f;
    _textView.text = text;
    [self.view addSubview:_textView];
    
    _textView.translatesAutoresizingMaskIntoConstraints = NO;
    NSArray<NSLayoutConstraint *> *textViewConstraints = @[
        [_textView.topAnchor constraintEqualToAnchor: self.view.topAnchor],
        [_textView.bottomAnchor constraintEqualToAnchor: self.view.bottomAnchor],
        [_textView.leadingAnchor constraintEqualToAnchor: self.view.leadingAnchor],
        [_textView.trailingAnchor constraintEqualToAnchor: self.view.trailingAnchor]
    ];
    [NSLayoutConstraint activateConstraints: textViewConstraints];
}

- (void)setOriginalImage:(UIImage *)originalImage {
    CGFloat viewWidth = self.view.doraemon_width;
    CGFloat viewHeight = self.view.doraemon_height;
    CGFloat imageWidth = originalImage.size.width;
    CGFloat imageHeight = originalImage.size.height;
    BOOL isPortrait = imageHeight / viewHeight > imageWidth / viewWidth;
    CGFloat scaledImageWidth, scaledImageHeight;
    CGFloat x,y;
    CGFloat imageScale;
    if (isPortrait) {//图片竖屏分量比较大
        imageScale = imageHeight / viewHeight;
        scaledImageHeight = viewHeight;
        scaledImageWidth = imageWidth / imageScale;
        x = (viewWidth - scaledImageWidth) / 2;
        y = 0;
    } else {//图片横屏分量比较大
        imageScale = imageWidth / viewWidth;
        scaledImageWidth = viewWidth;
        scaledImageHeight = imageHeight / imageScale;
        x = 0;
        y = (viewHeight - scaledImageHeight) / 2;
    }
    _imageView = [[UIImageView alloc] initWithFrame:CGRectMake(x, y, scaledImageWidth, scaledImageHeight)];
    _imageView.image = originalImage;
    _imageView.userInteractionEnabled = YES;
    [self.view addSubview:_imageView];
}

- (void)setMediaFile:(NSString *)mediaFile {
    _playerView = [[AVPlayerViewController alloc] init];
    NSURL *sourceMediaURL = [NSURL fileURLWithPath:mediaFile];
    AVAsset *mediaAsset = [AVURLAsset URLAssetWithURL:sourceMediaURL options:nil];
    AVPlayerItem *playerItem = [AVPlayerItem playerItemWithAsset:mediaAsset];
    AVPlayer *player = [AVPlayer playerWithPlayerItem:playerItem];
    
    self.playerView.player = player;
    self.playerView.view.translatesAutoresizingMaskIntoConstraints = YES;
    self.playerView.showsPlaybackControls = YES;
    self.playerView.view.bounds = self.view.bounds;
    [self.playerView.player play];
    
    [self addChildViewController:self.playerView];
    [self.view addSubview:self.playerView.view];
}

@end
