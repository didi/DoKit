//
//  DoraemonPageTimeInstance.h
//  DoraemonKit
//
//  Created by Frank on 2020/5/27.
//

#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN
@interface GKTZeusPageTimeRecord : NSObject
@property (nonatomic, copy) NSString *className;
@property (nonatomic, assign) float loadViewTime;
@property (nonatomic, assign) float viewDidLoadTime;
@property (nonatomic, assign) float viewWillAppearTime;
@property (nonatomic, assign) float viewWillDidAppearTime;
@property (nonatomic, assign) float viewDidLayoutSubviewsTime;
@property (nonatomic, copy) NSString *loadViewTimeDict;
@property (nonatomic, copy) NSString *viewDidLoadTimeDict;
@property (nonatomic, copy) NSString *viewWillAppearTimeDict;
@property (nonatomic, copy) NSString *viewWillDidAppearTimeDict;
@property (nonatomic, copy) NSString *viewDidLayoutSubviewsTimeDict;

@end


@interface DoraemonPageTimeInstance : NSObject

+ (instancetype)sharedInstance;

- (NSArray *)getArrayRecord;

- (void)timeWithVC:(id)vc sel:(SEL)sel;

@end

NS_ASSUME_NONNULL_END
