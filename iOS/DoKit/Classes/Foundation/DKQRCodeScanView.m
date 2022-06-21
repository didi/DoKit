/**
 * Copyright 2017 Beijing DiDi Infinity Technology and Development Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "DKQRCodeScanView.h"
#import <DoraemonKit/DKQRCodeScanLogic.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKQRCodeScanView ()

@property(nonatomic, strong, nullable) DKQRCodeScanLogic *qrCodeScanLogic;

@property(nonatomic, weak, nullable) AVCaptureVideoPreviewLayer *captureVideoPreviewLayer;

- (void)commonInit;

@end

NS_ASSUME_NONNULL_END

@implementation DKQRCodeScanView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    [self commonInit];

    return self;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        [self commonInit];
    }

    return self;
}

- (void)commonInit {
    self.backgroundColor = UIColor.blackColor;
}

- (void)layoutSubviews {
    [super layoutSubviews];

    self.captureVideoPreviewLayer.frame = self.bounds;
}

- (void)startScanQRCodeWithCompletionBlock:(void (^)(DKQRCodeScanResult, NSString *))completionBlock {
    self.qrCodeScanLogic = [[DKQRCodeScanLogic alloc] init];
    __weak typeof(self) weakSelf = self;
    __weak typeof(self.qrCodeScanLogic) weakQRCodeScanLogic = self.qrCodeScanLogic;
    [self.qrCodeScanLogic startScanQRCodeWithCompletionBlock:^(AVCaptureVideoPreviewLayer *_Nullable captureVideoPreviewLayer) {
        if (!weakSelf || !captureVideoPreviewLayer) {
            completionBlock(DKQRCodeScanResultAuthorityError, nil);
        } else {
            typeof(weakSelf) self = weakSelf;
            typeof(weakQRCodeScanLogic) qrCodeScanLogic = weakQRCodeScanLogic;
            qrCodeScanLogic.completionBlock = ^(NSString *_Nullable decodedString) {
                completionBlock(DKQRCodeScanResultSuccess, decodedString);
            };
            captureVideoPreviewLayer.frame = self.bounds;
            [self.layer addSublayer:captureVideoPreviewLayer];
        }
    }];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
