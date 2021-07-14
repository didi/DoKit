//
//  DoraemonMCXPath.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import "DoraemonMCXPathSerializer.h"

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

    NSMutableArray<DoraemonMCXPathNode *> *currentPathNodeList = [NSMutableArray array];
    
    UIView *currentV = view;
    BOOL isOnCell = NO;
    while (currentV && currentV != currentWindow) {
        if ([currentV isKindOfClass:[UITableViewCell class]]) {
            isOnCell = YES;
            UITableViewCell *cell = (UITableViewCell *)currentV;
            UITableView *tableView = nil;
            UIView *superV = cell.superview;
            while (superV) {
                if ([superV isKindOfClass:[UITableView class]]) {
                    tableView = (UITableView *)superV;
                    break;
                }
                superV = superV.superview;
            }
            if (tableView) {
                self.cellIndexPath = [tableView indexPathForCell:cell];
            }
            currentV = tableView;
            self.pathNodeList = currentPathNodeList.copy;
            [currentPathNodeList removeAllObjects];
            
            break;
        }
        
        DoraemonMCXPathNode *node = [[DoraemonMCXPathNode alloc] init];
        node.className = NSStringFromClass(currentV.class);
        node.index = [currentV.superview.subviews indexOfObject:currentV];
        [currentPathNodeList insertObject:node atIndex:0];
        
        currentV = currentV.superview;
    }
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
    switch (components.count) {
        case 2:
        {
            self.windowIndex = [components.firstObject integerValue];
            NSArray *nodes = [components.lastObject componentsSeparatedByString:@"/"];
            NSMutableArray *arrM = [NSMutableArray array];
            [nodes enumerateObjectsUsingBlock:^(NSString *_Nonnull nodeString, NSUInteger idx, BOOL * _Nonnull stop) {
                NSArray *nodeComponents =[nodeString componentsSeparatedByString:@"&"];
                if (nodeComponents.count == 2) {
                    DoraemonMCXPathNode *node = [[DoraemonMCXPathNode alloc] init];
                    node.index = [nodeComponents.firstObject integerValue];
                    node.className = nodeComponents.lastObject;
                    [arrM addObject:node];
                }
            }];
            self.pathNodeList = arrM.copy;
        }
            break;
        case 4:
        {
            self.windowIndex = [components.firstObject integerValue];
            NSArray *nodes = [components[1] componentsSeparatedByString:@"/"];
            NSMutableArray *arrM = [NSMutableArray array];
            [nodes enumerateObjectsUsingBlock:^(NSString *_Nonnull nodeString, NSUInteger idx, BOOL * _Nonnull stop) {
                NSArray *nodeComponents =[nodeString componentsSeparatedByString:@"&"];
                if (nodeComponents.count == 2) {
                    DoraemonMCXPathNode *node = [[DoraemonMCXPathNode alloc] init];
                    node.index = [nodeComponents.firstObject integerValue];
                    node.className = nodeComponents.lastObject;
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
                NSArray *nodeComponents =[nodeString componentsSeparatedByString:@"&"];
                if (nodeComponents.count == 2) {
                    DoraemonMCXPathNode *node = [[DoraemonMCXPathNode alloc] init];
                    node.index = [nodeComponents.firstObject integerValue];
                    node.className = nodeComponents.lastObject;
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

/**
 字符串拼装格式
 1-1&UIView/2&UIView/3UIImage/1&UITabeView-5/3-3&UIView/2&UIButton/4&UILabel
 windowIndex-控件或list容器的索引路径与class名称路径-控件所在cell的索引section/row_控件在cell上的索引路径与class名称路径
 */
- (NSString *)generalPathToTransfer {
    if (self.windowIndex == NSNotFound) {
        return @"";
    }
    NSString *resultPathString = @"";
    
    __block NSString *pathString = @"";
    
    [self.pathNodeList enumerateObjectsUsingBlock:^(DoraemonMCXPathNode * _Nonnull node, NSUInteger idx, BOOL * _Nonnull stop) {
        pathString = [pathString stringByAppendingFormat:@"%zd&%@%@",node.index,node.className , (self.listContainerPathNodeList.count == (idx+1))?@"":@"/"];
    }];
    
    if (self.isOneCell) {
        __block NSString *listContainerPath = @"";
        
        [self.listContainerPathNodeList enumerateObjectsUsingBlock:^(DoraemonMCXPathNode * _Nonnull node, NSUInteger idx, BOOL * _Nonnull stop) {
            listContainerPath = [listContainerPath stringByAppendingFormat:@"%zd&%@%@",node.index,node.className , (self.listContainerPathNodeList.count == (idx+1))?@"":@"/"];
        }];
        resultPathString = [NSString stringWithFormat:@"%zd-%@-%zd/%zd-%@",
                            self.windowIndex,
                            listContainerPath,
                            self.cellIndexPath.section,
                            self.cellIndexPath.row,
                            pathString];
    }else {
        resultPathString = [NSString stringWithFormat:@"%zd-%@",self.windowIndex,pathString];
    }
    
    return resultPathString;
}

- (UIView *)fetchView {
    UIWindow *rootWidow = nil;
    if ([UIApplication sharedApplication].windows.count > self.windowIndex) {
        rootWidow = [[UIApplication sharedApplication].windows objectAtIndex:self.windowIndex];
    }
    
    if (rootWidow == nil) {
        return nil;
    }
    
    if (self.isOneCell) {
        __block BOOL notMatch = NO;
        __block UIView *currentV = rootWidow;
        [self.listContainerPathNodeList enumerateObjectsUsingBlock:^(DoraemonMCXPathNode * _Nonnull node, NSUInteger idx, BOOL * _Nonnull stop) {
            if (currentV != nil && currentV.subviews.count > node.index) {
                UIView *tempV = currentV.subviews[node.index];
                if ([NSStringFromClass(tempV.class) isEqualToString:node.className]) {
                    currentV = tempV;
                }else {
                    notMatch = YES;
                    *stop = YES;
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
            if (currentVOnCell != nil && currentVOnCell.subviews.count > node.index) {
                UIView *tempV = currentVOnCell.subviews[node.index];
                if ([NSStringFromClass(tempV.class) isEqualToString:node.className]) {
                    currentVOnCell = tempV;
                }else {
                    notMatchOnCell = YES;
                    *stop = YES;
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
            if (currentV != nil && currentV.subviews.count > node.index) {
                UIView *tempV = currentV.subviews[node.index];
                if ([NSStringFromClass(tempV.class) isEqualToString:node.className]) {
                    currentV = tempV;
                }else {
                    notMatch = YES;
                    *stop = YES;
                }
            }else {
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

@end
