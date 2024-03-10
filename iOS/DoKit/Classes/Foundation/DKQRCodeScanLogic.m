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

#import <AVFoundation/AVFoundation.h>
#import "DKQRCodeScanLogic.h"

NS_ASSUME_NONNULL_BEGIN

static void requestVideoGrant(void (^completionBlock)(BOOL isGranted));

static inline void safeMainThread(dispatch_block_t block);

@interface DKQRCodeScanLogic () <AVCaptureMetadataOutputObjectsDelegate>

@property(nonatomic, nullable, strong) AVCaptureSession *captureSession;

@end

NS_ASSUME_NONNULL_END

void requestVideoGrant(void (^completionBlock)(BOOL isGranted)) {
    AVAuthorizationStatus authorizationStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (authorizationStatus == AVAuthorizationStatusNotDetermined) {
        // Request system authority.
        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
            safeMainThread(^{
                completionBlock(granted);
            });
        }];
    } else {
        completionBlock(authorizationStatus == AVAuthorizationStatusAuthorized);
    }
}

void safeMainThread(dispatch_block_t block) {
    if (!NSThread.isMainThread) {
        dispatch_async(dispatch_get_main_queue(), block);
    } else {
        block();
    }
}

@implementation DKQRCodeScanLogic

- (void)startScanQRCodeWithCompletionBlock:(void (^)(AVCaptureVideoPreviewLayer *_Nullable captureVideoPreviewLayer))completionBlock {
    __weak typeof(self) weakSelf = self;
    requestVideoGrant(^(BOOL isGranted) {
        if (!weakSelf) {
            completionBlock(nil);

            return;
        }
        typeof(weakSelf) self = weakSelf;
        if (!isGranted) {
            completionBlock(nil);
        } else {
            AVCaptureDevice *captureDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];

            NSCAssert(captureDevice, @"+[AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo] return nil.");

            NSError *error = nil;
            AVCaptureDeviceInput *captureDeviceInput = [[AVCaptureDeviceInput alloc] initWithDevice:captureDevice error:&error];

            NSCAssert(!error && captureDeviceInput, @"AVCaptureDeviceInput creation is failed.");

            AVCaptureMetadataOutput *captureMetadataOutput = [[AVCaptureMetadataOutput alloc] init];
            [captureMetadataOutput setMetadataObjectsDelegate:self queue:dispatch_get_main_queue()];

            self.captureSession = [[AVCaptureSession alloc] init];
            [self.captureSession beginConfiguration];

            NSCAssert([self.captureSession canAddInput:captureDeviceInput], @"-[captureSession canAddInput:captureDeviceInput] is failed.");
            [self.captureSession addInput:captureDeviceInput];

            NSCAssert([self.captureSession canAddOutput:captureMetadataOutput], @"-[AVCaptureSession canAddOutput:captureMetadataOutput] return NO.");
            [self.captureSession addOutput:captureMetadataOutput];
            if ([captureMetadataOutput.availableMetadataObjectTypes containsObject:AVMetadataObjectTypeQRCode]) {
                captureMetadataOutput.metadataObjectTypes = @[AVMetadataObjectTypeQRCode];
            }
            [self.captureSession commitConfiguration];

            AVCaptureVideoPreviewLayer *captureVideoPreviewLayer = [AVCaptureVideoPreviewLayer layerWithSession:self.captureSession];
            completionBlock(captureVideoPreviewLayer);

            [self.captureSession startRunning];
        }
    });
}

- (void)captureOutput:(AVCaptureOutput *)output didOutputMetadataObjects:(NSArray<__kindof AVMetadataObject *> *)metadataObjects fromConnection:(AVCaptureConnection *)connection {
    if (metadataObjects.count > 0) {
        AVMetadataObject *metadataObject = metadataObjects.firstObject;
        if ([metadataObject.type isEqualToString:AVMetadataObjectTypeQRCode] && [metadataObject isKindOfClass:AVMetadataMachineReadableCodeObject.class]) {
            [self.captureSession stopRunning];
            AVMetadataMachineReadableCodeObject *metadataMachineReadableCodeObject = (AVMetadataMachineReadableCodeObject *) metadataObject;
            self.completionBlock ? self.completionBlock(metadataMachineReadableCodeObject.stringValue) : (void) nil;
        } else {
            goto exit;
        }
    } else {
        goto exit;
    }

    return;

    exit:
    self.completionBlock ? self.completionBlock(nil) : (void) nil;
}

@end
