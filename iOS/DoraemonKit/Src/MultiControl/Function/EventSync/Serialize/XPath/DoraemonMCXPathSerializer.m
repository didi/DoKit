//
//  DoraemonMCXPath.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import "DoraemonMCXPathSerializer.h"
#import <WebKit/WebKit.h>

static NSUInteger const kDoraemonMCXPathUseKeyWindowIndex = 99999;
@interface DoraemonMCXPathSerializer ()

// 如果isOneCell为true , 这个字段有值 列表容器的XPath 例如 UITableView , UICollectionView
@property (nonatomic , strong) NSArray<DoraemonMCXPathNode *> *listContainerPathNodeList;

// 从根window到当前控件的XPath 如果isOneCell为true , 就代表从listContainer到当前控件的XPath
@property (nonatomic , copy ) NSArray<DoraemonMCXPathNode *> *pathNodeList;

@end

@implementation DoraemonMCXPathSerializer

+ (instancetype )xPathInstanceWithView:(UIView *)view {
    return [[self alloc] initWithView:view];;
}

/// 解析网络上传过来的xpath字符串信息,生成xPath对象
+ (instancetype)xPathInstanceFromXpath:(NSString *)xpath {
    return [[self alloc] initWithXpathString:xpath];
}

+ (NSString *)xPathStringWithView:(UIView *)view {
    return [[self xPathInstanceWithView:view] generalPathToTransfer];
}

+ (UIView *)viewFromXpath:(NSString *)xpath {
    return [[self xPathInstanceFromXpath:xpath] fetchView];
}

- (instancetype)initWithView:(UIView *)view {
    if (self = [super init]) {
        self.windowIndex = NSNotFound;
        [self parseView:view];
    }
    return self;
}

- (instancetype)initWithXpathString:(NSString *)xPathString {
    if (self = [super init]) {
        self.windowIndex = NSNotFound;
        [self parseXpathString:xPathString];
    }
    return self;
}

- (NSArray<Class> *)ignoreContainerClasses {
    return @[
        [UIWebView class],
        [WKWebView class]
    ];
}

- (void)parseView:(UIView *)view {
    UIWindow *currentWindow = nil;
    if ([view isKindOfClass:[UIWindow class]] && view.window == nil) {
        currentWindow = (UIWindow *)view;
    }else {
        currentWindow = view.window;
    }
    
    if (currentWindow == nil) {
        return;
    }
    
    self.vcCls = [self.class ownerVCWithView:view];
    
    self.windowIndex = [[[UIApplication sharedApplication] windows] indexOfObject:currentWindow];
    if (self.windowIndex == NSNotFound &&
        [UIApplication sharedApplication].keyWindow == currentWindow) {
        self.windowIndex = kDoraemonMCXPathUseKeyWindowIndex;
    }
    self.windowClsName = NSStringFromClass(currentWindow.class);
    self.windowRootVCClsName = currentWindow.rootViewController ? NSStringFromClass(currentWindow.rootViewController.class) : @"null";
    NSMutableArray<DoraemonMCXPathNode *> *currentPathNodeList = [NSMutableArray array];
    
    UIView *currentV = view;
    BOOL isOnCell = NO;

    while (currentV && currentV != currentWindow) {
        
        __block BOOL shouldIgnore = NO;
        [[self ignoreContainerClasses] enumerateObjectsUsingBlock:^(Class  _Nonnull cls, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([currentV isKindOfClass:cls]) {
                shouldIgnore = YES;
                *stop = YES;
            }
        }];
        if (shouldIgnore) {
            self.ignore = YES;
            break;
        }
        
        NSDictionary *reuseViewMap = @{
            NSStringFromClass(UICollectionViewCell.class) : UICollectionView.class,
            NSStringFromClass(UITableViewCell.class) : UITableView.class,
        };
        __block Class resueViewCls =  reuseViewMap[NSStringFromClass(currentV.class)];
        if (resueViewCls) {
            isOnCell = YES;
            UIView *cell = (UITableViewCell *)currentV;
            UIView *reuseViewInstance = nil;
            UIView *superV = cell.superview;
            while (superV) {
                if ([superV isKindOfClass:resueViewCls]) {
                    reuseViewInstance = (UITableView *)superV;
                    break;
                }
                superV = superV.superview;
            }
            if (reuseViewInstance && [reuseViewInstance respondsToSelector:@selector(indexPathForCell:)]) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                self.cellIndexPath = [reuseViewInstance performSelector:@selector(indexPathForCell:) withObject:cell];
#pragma clang diagnostic pop
            }
            currentV = reuseViewInstance;
            self.pathNodeList = currentPathNodeList.copy;
            [currentPathNodeList removeAllObjects];
            
            continue;
        }
        
        DoraemonMCXPathNode *node = [[DoraemonMCXPathNode alloc] init];
        node.className = NSStringFromClass(currentV.class);
        node.index = [currentV.superview.subviews indexOfObject:currentV];
        [currentPathNodeList insertObject:node atIndex:0];
        
        currentV = currentV.superview;
    }
    
    if (self.ignore) {
        return;
    }
    
    self.isOneCell = isOnCell;
    
    if (isOnCell) {
        self.listContainerPathNodeList = currentPathNodeList.copy;
    }else {
        self.pathNodeList = currentPathNodeList.copy;
    }
}


- (void)parseXpathString:(NSString *)xPath {
    if (![xPath isKindOfClass:[NSString class]] ||
        xPath.length == 0) {
        return;
    }
    NSArray *components = [xPath componentsSeparatedByString:@"-"];
    if (components.count > 0) {
        NSString *windowInfo = components.firstObject;
        NSArray *windowInfoArr = [windowInfo componentsSeparatedByString:@"/"];
        self.windowIndex = [windowInfoArr[0] integerValue];
        self.windowClsName = windowInfoArr[1];
        self.windowRootVCClsName = windowInfoArr[2];
    }
    switch (components.count) {
        case 2:
        {
            NSArray *nodes = [components.lastObject componentsSeparatedByString:@"/"];
            NSMutableArray *arrM = [NSMutableArray array];
            [nodes enumerateObjectsUsingBlock:^(NSString *_Nonnull nodeString, NSUInteger idx, BOOL * _Nonnull stop) {
                DoraemonMCXPathNode *node = [DoraemonMCXPathNode nodeWithString:nodeString];
                if (node) {
                    [arrM addObject:node];
                }
            }];
            self.pathNodeList = arrM.copy;
        }
            break;
        case 4:
        {
            NSArray *nodes = [components[1] componentsSeparatedByString:@"/"];
            NSMutableArray *arrM = [NSMutableArray array];
            [nodes enumerateObjectsUsingBlock:^(NSString *_Nonnull nodeString, NSUInteger idx, BOOL * _Nonnull stop) {
                DoraemonMCXPathNode *node = [DoraemonMCXPathNode nodeWithString:nodeString];
                if (node) {
                    [arrM addObject:node];
                }
            }];
            self.listContainerPathNodeList = arrM.copy;
            
            NSArray *indexPathComponents = [components[2] componentsSeparatedByString:@"/"];
            self.isOneCell = YES;
            if (indexPathComponents.count == 2) {
                self.cellIndexPath = [NSIndexPath indexPathForRow:[indexPathComponents.lastObject integerValue] inSection:[indexPathComponents.firstObject integerValue]];
            }
            
            nodes = [components.lastObject componentsSeparatedByString:@"/"];
            arrM = [NSMutableArray array];
            [nodes enumerateObjectsUsingBlock:^(NSString *_Nonnull nodeString, NSUInteger idx, BOOL * _Nonnull stop) {
                DoraemonMCXPathNode *node = [DoraemonMCXPathNode nodeWithString:nodeString];
                if (node) {
                    [arrM addObject:node];
                }
            }];
            self.pathNodeList = arrM.copy;
            
        }
            break;
        default:
            break;
    }

}

- (NSString *)pathStringWitgNode:(DoraemonMCXPathNode *)node isLastNode:(BOOL)isLastNode{
    return [NSString stringWithFormat:@"%zd&%@%@",
            node.index,
            node.className ,
            isLastNode?@"":@"/"];
}

/**
 字符串拼装格式
 1-1&UIView&2&3&0/2&UIView&2&3&0/3UIImage&2&3&0/1&UITabeView&2&3&0-5/3-3&UIView&2&3&0/2&UIButton&2&3&0/4&UILabel&2&3&0
 windowIndex-控件或list容器的索引路径与class名称路径-控件所在cell的索引section/row_控件在cell上的索引路径与class名称路径
 */
- (NSString *)generalPathToTransfer {
    if (self.windowIndex == NSNotFound) {
        return @"";
    }
    NSString *resultPathString = @"";
    
    __block NSString *pathString = @"";
    
    [self.pathNodeList enumerateObjectsUsingBlock:^(DoraemonMCXPathNode * _Nonnull node, NSUInteger idx, BOOL * _Nonnull stop) {
        pathString = [pathString stringByAppendingString:[self pathStringWitgNode:node
                                                                       isLastNode:(self.pathNodeList.count == (idx+1))]];
    }];
    
    if (self.isOneCell) {
        __block NSString *listContainerPath = @"";
        
        [self.listContainerPathNodeList enumerateObjectsUsingBlock:^(DoraemonMCXPathNode * _Nonnull node, NSUInteger idx, BOOL * _Nonnull stop) {
            listContainerPath = [listContainerPath stringByAppendingString:[self pathStringWitgNode:node
                                                                           isLastNode:(self.listContainerPathNodeList.count == (idx+1))]];

        }];
        resultPathString = [NSString stringWithFormat:@"%zd/%@/%@-%@-%zd/%zd-%@",
                            self.windowIndex,
                            self.windowClsName,
                            self.windowRootVCClsName,
                            listContainerPath,
                            self.cellIndexPath.section,
                            self.cellIndexPath.row,
                            pathString];
    }else {
        resultPathString = [NSString stringWithFormat:@"%zd/%@/%@-%@",self.windowIndex,self.windowClsName,self.windowRootVCClsName,pathString];
    }
    
    return resultPathString;
}

- (BOOL)isWindowMatch:(UIWindow *)window {
    if (!window) {
        return NO;
    }
    
    if (![NSStringFromClass(window.class) isEqualToString:self.windowClsName]) {
        return NO;
    }
    
    if (window.rootViewController == nil) {
        return [self.windowClsName isEqualToString:@"null"];
    }
    
    return [NSStringFromClass(window.rootViewController.class) isEqualToString:self.windowRootVCClsName];
    
}

- (BOOL)isMatchWithView:(UIView *)view node:(DoraemonMCXPathNode *)node {
    return YES;
}

- (UIView *)fetchView {
    UIWindow *rootWidow = nil;
    if ([UIApplication sharedApplication].windows.count > self.windowIndex) {
        rootWidow = [[UIApplication sharedApplication].windows objectAtIndex:self.windowIndex];
        if (![self isWindowMatch:rootWidow]) {
            rootWidow = nil;
            NSInteger i = 1 ;
            while (true) {
                if ((self.windowIndex - i) >= 0) {
                    UIWindow *tempWindow = [UIApplication sharedApplication].windows[self.windowIndex - i];
                    if ([self isWindowMatch:tempWindow]) {
                        rootWidow = tempWindow ;
                        break;
                    }
                }
                
                if ((self.windowIndex + i) < [UIApplication sharedApplication].windows.count) {
                    UIWindow *tempWindow = [UIApplication sharedApplication].windows[self.windowIndex + i];
                    if ([self isWindowMatch:tempWindow]) {
                        rootWidow = tempWindow ;
                        break;
                    }
                }
                i ++ ;
                if ((self.windowIndex - i) < 0 && [UIApplication sharedApplication].windows.count <= (i + self.windowIndex)) {
                    break;
                }
            }
        }
    }
    if (kDoraemonMCXPathUseKeyWindowIndex == self.windowIndex) {
        rootWidow = [UIApplication sharedApplication].keyWindow;
    }
    
    if (rootWidow == nil) {
        return nil;
    }
    
    if (self.isOneCell) {
        __block BOOL notMatch = NO;
        __block UIView *currentV = rootWidow;
        [self.listContainerPathNodeList enumerateObjectsUsingBlock:^(DoraemonMCXPathNode * _Nonnull node, NSUInteger idx, BOOL * _Nonnull stop) {
            if (currentV != nil ) {
                UIView *tempV = nil;
                if (currentV.subviews.count > node.index) {
                    tempV = currentV.subviews[node.index];
                }
                if (tempV && [self isMatchWithView:tempV node:node]) {
                    currentV = tempV;
                }else {
                    __block UIView *resultV = nil;
                    [currentV.subviews enumerateObjectsUsingBlock:^(__kindof UIView * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                        if ([self isMatchWithView:obj node:node]) {
                            resultV = obj;
                            *stop = YES;
                        }
                    }];
                    if (resultV) {
                        currentV = resultV;
                    }else {
                        notMatch = YES;
                        *stop = YES;
                    }
                }
            }else {
                notMatch = YES;
                *stop = YES;
            }
        }];
        if (notMatch ||
            !([currentV isKindOfClass:[UITableView class]] ||
              [currentV isKindOfClass:[UICollectionView class]])) {
            return nil;
        }
        
        __block BOOL notMatchOnCell = NO;
        __block UIView *currentVOnCell = nil;

        if ([currentV isKindOfClass:[UITableView class]]) {
            UITableView *tableView = (UITableView *)currentV;
            UITableViewCell *cell = [tableView cellForRowAtIndexPath:self.cellIndexPath];
            currentVOnCell = cell;
        }else if ([currentV isKindOfClass:[UICollectionView class]]) {
            UICollectionView *collectionView = (UICollectionView *)currentV;
            UICollectionViewCell *cell = [collectionView cellForItemAtIndexPath:self.cellIndexPath];
            currentVOnCell = cell;
        }
        

        [self.pathNodeList enumerateObjectsUsingBlock:^(DoraemonMCXPathNode * _Nonnull node, NSUInteger idx, BOOL * _Nonnull stop) {
            if (currentVOnCell != nil) {
                UIView *tempV = nil;
                if (currentVOnCell.subviews.count > node.index) {
                    tempV = currentVOnCell.subviews[node.index];
                }
                if (tempV && [self isMatchWithView:tempV node:node]) {
                    currentVOnCell = tempV;
                }else {
                    __block UIView *resultV = nil;
                    [currentVOnCell.subviews enumerateObjectsUsingBlock:^(__kindof UIView * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                        if ([self isMatchWithView:obj node:node]) {
                            resultV = obj;
                            *stop = YES;
                        }
                    }];
                    if (resultV) {
                        currentVOnCell = resultV;
                    }else {
                        notMatchOnCell = YES;
                        *stop = YES;
                    }
                }
            }else {
                notMatchOnCell = YES;
                *stop = YES;
            }
        }];
        if (notMatchOnCell) {
            return nil;
        }
        return currentVOnCell;
        
    }else {
        __block BOOL notMatch = NO;
        __block UIView *currentV = rootWidow;
        [self.pathNodeList enumerateObjectsUsingBlock:^(DoraemonMCXPathNode * _Nonnull node, NSUInteger idx, BOOL * _Nonnull stop) {
             __block UIView *tempV =  nil ;
            if (currentV != nil && currentV.subviews.count > node.index) {
                tempV = currentV.subviews[node.index];
                if ([self isMatchWithView:tempV node:node]) {
                    currentV = tempV;
                }else {
                    tempV = nil;
                }
            }
            if (!tempV) {
                [currentV.subviews enumerateObjectsUsingBlock:^(__kindof UIView * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    if ([self isMatchWithView:obj node:node]) {
                        tempV = obj;
                        currentV = tempV;
                    }
                }];
            }
            
            if (tempV == nil) {
                notMatch = YES;
                *stop = YES;
            }
        }];
        if (notMatch) {
            return nil;
        }
        return currentV;
    }
}

+ (UIViewController *)ownerVCWithView:(UIView *)view {
    
    UIResponder *currentResponder = view.nextResponder;
    while (![currentResponder isKindOfClass:[UIViewController class]] && currentResponder) {
        currentResponder = currentResponder.nextResponder;
    }
    return (UIViewController *)currentResponder;
}

@end

@implementation DoraemonMCXPathNode

+ (instancetype)nodeWithString:(NSString *)string {
    NSArray *nodeComponents =[string componentsSeparatedByString:@"&"];
    if (nodeComponents.count == 2) {
        DoraemonMCXPathNode *node = [[self alloc] init];
        node.index = [nodeComponents[0] integerValue];
        node.className = nodeComponents[1];
        return node;
    }
    return nil;
}

@end
