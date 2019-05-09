//
//  RunTraceHelp.m
//  RunTrace
//
//  Created by 孙昕 on 15/9/18.
//  Copyright (c) 2015年 孙昕. All rights reserved.
//

#import "RunTraceHelp.h"
NSString *msgRunTraceSuperView=@"msgRunTraceSuperView";
NSString *msgRunTraceSubView=@"msgRunTraceSubView";
NSString *msgRunTraceRemoveView=@"msgRunTraceRemoveView";
NSString *msgRunTraceRemoveSubView=@"msgRunTraceRemoveSubView";
NSString *msgRunTraceAddSubView=@"msgRunTraceAddSubView";
NSString *msgRunTraceContraints=@"msgRunTraceContraints";
NSString *msgRunTraceInfoPosition=@"msgRunTraceInfoPosition";
@implementation RunTraceObject
+(instancetype)objectWithWeak:(id)o
{
    RunTraceObject *obj=[[RunTraceObject alloc] init];
    obj.object=o;
    return obj;
}


@end
@interface RunTraceHelp()<UITableViewDelegate,UITableViewDataSource>
{
    NSMutableArray *arrSuper;
    NSMutableArray *arrSub;
    BOOL bTouch;
    CGFloat left,top;
    CGRect originFrame;
    UITableView *tableTrace;
    NSMutableArray* arrTrace;
    __weak UIView *viewTrack;
    CGFloat viewTrackBorderWidth;
    UIColor* ViewTrackBorderColor;
    UITableView *tableAutoLayout;
    NSMutableArray *arrAutoLayout;
    __weak UIView *viewAutoLayout;
}
@end
@implementation RunTraceHelp

-(void)willMoveToWindow:(UIWindow *)newWindow
{
    if(newWindow!=nil)
    {
        bTouch=NO;
        self.clipsToBounds=YES;
        self.translatesAutoresizingMaskIntoConstraints=YES;
        self.layer.borderWidth=2;
        self.layer.borderColor=[UIColor blackColor].CGColor;
        arrSuper=[[NSMutableArray alloc] initWithCapacity:30];
        _lbAutoLayout.text=[NSString stringWithFormat:@"%@",_viewHit.translatesAutoresizingMaskIntoConstraints?@"NO":@"YES"];
        if(_viewHit.translatesAutoresizingMaskIntoConstraints)
        {
            _btnAutoLayout.hidden=YES;
        }
        else
        {
            _btnAutoLayout.hidden=NO;
        }
        UIView *viewSuper=_viewHit;
        while ((viewSuper=viewSuper.superview)) {
            [arrSuper addObject:[RunTraceObject objectWithWeak:viewSuper] ];
        }
        _tableSuper.delegate=self;
        _tableSuper.dataSource=self;
        [_tableSuper reloadData];
        _tableSuper.tableFooterView=[[UIView alloc] init];
        arrSub=[[NSMutableArray alloc] initWithCapacity:30];
        for(UIView *subview in _viewHit.subviews)
        {
            [arrSub addObject:[RunTraceObject objectWithWeak:subview]];
        }
        _tableSub.delegate=self;
        _tableSub.dataSource=self;
        [_tableSub reloadData];
        _tableSub.tableFooterView=[[UIView alloc] init];
        arrTrace=[[NSMutableArray alloc] initWithCapacity:30];
        arrAutoLayout=[[NSMutableArray alloc] initWithCapacity:30];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleRemoveView:) name:msgRunTraceRemoveView object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleRemoveSubView:) name:msgRunTraceRemoveSubView object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleAddSubView:) name:msgRunTraceAddSubView object:nil];
    }
    else
    {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
        if(viewTrack)
        {
            [self removeObserver];
            viewTrack.layer.borderColor=ViewTrackBorderColor.CGColor;
            viewTrack.layer.borderWidth=viewTrackBorderWidth;
        }
    }
}

-(void)handleRemoveSubView:(NSNotification*)nofi
{
    UIView *view=nofi.object;
    if(view==viewTrack)
    {
        view=nofi.userInfo[@"subview"];
        [arrTrace addObject:@{
                              @"Key":@"Remove SubView",
                              @"Time":[self currentDate],
                              @"OldValue":[NSString stringWithFormat:@"%@(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",NSStringFromClass([view class]),view.frame.origin.x,view.frame.origin.y,view.frame.size.width,view.frame.size.height],
                              @"NewValue":@"nil"
                              }];
        [tableTrace reloadData];
        tableTrace.tableFooterView=[[UIView alloc] init];
    }
}

-(void)handleAddSubView:(NSNotification*)nofi
{
    UIView *view=nofi.object;
    if(view==viewTrack)
    {
        view=nofi.userInfo[@"subview"];
        [arrTrace addObject:@{
                              @"Key":@"Add SubView",
                              @"Time":[self currentDate],
                              @"OldValue":@"nil",
                              @"NewValue":[NSString stringWithFormat:@"%@(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",NSStringFromClass([view class]),view.frame.origin.x,view.frame.origin.y,view.frame.size.width,view.frame.size.height]
                              }];
        [tableTrace reloadData];
        tableTrace.tableFooterView=[[UIView alloc] init];
    }
}

-(void)handleRemoveView:(NSNotification*)nofi
{
    UIView *view=nofi.object;
    if(view==viewTrack)
    {
        [self removeObserver];
        viewTrack.layer.borderColor=ViewTrackBorderColor.CGColor;
        viewTrack.layer.borderWidth=viewTrackBorderWidth;
        [arrTrace addObject:@{
                              @"Key":@"RemoveFromSuperview",
                              @"Time":[self currentDate],
                              @"OldValue":[NSString stringWithFormat:@"%@(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",NSStringFromClass([view class]),view.frame.origin.x,view.frame.origin.y,view.frame.size.width,view.frame.size.height],
                              @"NewValue":@"nil"
                              }];
        [tableTrace reloadData];
        tableTrace.tableFooterView=[[UIView alloc] init];
        [arrSuper removeAllObjects];
        [arrSub removeAllObjects];
        [_tableSuper reloadData];
        _tableSuper.tableFooterView=[[UIView alloc] init];
        [_tableSub reloadData];
        _tableSub.tableFooterView=[[UIView alloc] init];
        viewTrack=nil;
        
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"RunTrace" message:[NSString stringWithFormat:@"%@ RemoveFromSuperview",NSStringFromClass([view class])] preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction * okAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:nil];
        [alert addAction:okAction];
        [self.window.rootViewController presentViewController:alert animated:YES completion:nil];
    }
}

- (IBAction)onClose:(id)sender
{
    if([[_btnClose titleForState:UIControlStateNormal] isEqualToString:@"Close"])
    {
        [self removeFromSuperview];
    }
    else if([[_btnClose titleForState:UIControlStateNormal] isEqualToString:@"Stop"])
    {
        [tableTrace removeFromSuperview];
        [arrTrace removeAllObjects];
        [_btnClose setTitle:@"Close" forState:UIControlStateNormal];
        [_btnTrace setTitle:@"Trace" forState:UIControlStateNormal];
        _lbTip.text=@"SuperViews:";
        tableTrace=nil;
        if(_viewHit.translatesAutoresizingMaskIntoConstraints)
        {
            _btnAutoLayout.hidden=YES;
        }
        else
        {
            _btnAutoLayout.hidden=NO;
        }
        if(viewTrack)
        {
            viewTrack.layer.borderColor=ViewTrackBorderColor.CGColor;
            viewTrack.layer.borderWidth=viewTrackBorderWidth;
            [self removeObserver];
            viewTrack=nil;
        }
        else
        {
            [self removeFromSuperview];
        }
    }
    else if([[_btnClose titleForState:UIControlStateNormal] isEqualToString:@"Back"])
    {
        viewAutoLayout=nil;
        [tableAutoLayout removeFromSuperview];
        [arrAutoLayout removeAllObjects];
        [_btnClose setTitle:@"Close" forState:UIControlStateNormal];
        _btnTrace.hidden=NO;
        [_btnTrace setTitle:@"Trace" forState:UIControlStateNormal];
        _lbTip.text=@"SuperViews:";
        tableAutoLayout=nil;
        if(_viewHit.translatesAutoresizingMaskIntoConstraints)
        {
            _btnAutoLayout.hidden=YES;
        }
        else
        {
            _btnAutoLayout.hidden=NO;
        }
    }
    
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if(tableView==_tableSuper)
    {
        return  arrSuper.count;
    }
    else if(tableView==_tableSub)
    {
        return arrSub.count;
    }
    else if(tableView==tableTrace)
    {
        return arrTrace.count;
    }
    else if(tableView==tableAutoLayout)
    {
        return arrAutoLayout.count;
    }
    else
    {
        return 0;
    }
}

-(UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UIView *view;
    UITableViewCell *cell;
    if(tableView==_tableSuper)
    {
        NSString *cellID=@"RunTraceHelpSuperCell";
        cell=[tableView dequeueReusableCellWithIdentifier:cellID];
        if(cell==nil)
        {
            cell=[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:cellID];
        }
        view=((RunTraceObject*)arrSuper[indexPath.row]).object;
    }
    else if(tableView==_tableSub)
    {
        NSString *cellID=@"RunTraceHelpSubCell";
        cell=[tableView dequeueReusableCellWithIdentifier:cellID];
        if(cell==nil)
        {
            cell=[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:cellID];
        }
        view=((RunTraceObject*)arrSub[indexPath.row]).object;
    }
    else if(tableView==tableTrace)
    {
        NSString *cellID=@"RunTraceHelpTraceCell";
        cell=[tableView dequeueReusableCellWithIdentifier:cellID];
        if(cell==nil)
        {
            cell=[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:cellID];
        }
        NSDictionary *dic=arrTrace[indexPath.row];
        cell.textLabel.numberOfLines=0;
        cell.textLabel.lineBreakMode=NSLineBreakByCharWrapping;
        cell.textLabel.frame=CGRectMake(0, 0, tableView.frame.size.width, 40);
        cell.textLabel.text=[NSString stringWithFormat:@"%@(%@)",dic[@"Key"],dic[@"Time"]];
        [cell.textLabel sizeToFit];
        cell.detailTextLabel.numberOfLines=0;
        cell.detailTextLabel.lineBreakMode=NSLineBreakByCharWrapping;
        cell.textLabel.frame=CGRectMake(0, 40, tableView.frame.size.width, 30);
        cell.detailTextLabel.text=[NSString stringWithFormat:@"from %@ to %@",dic[@"OldValue"],dic[@"NewValue"]];
        if([dic[@"Key"] isEqualToString:@"superview.frame"])
        {
            cell.detailTextLabel.text=[[NSString stringWithFormat:@"%@ ",dic[@"Superview"]] stringByAppendingString:cell.detailTextLabel.text];
        }
        [cell.detailTextLabel sizeToFit];
        return cell;
    }
    else if(tableView==tableAutoLayout)
    {
        NSString *cellID=@"RunTraceHelpAutoLayoutCell";
        cell=[tableView dequeueReusableCellWithIdentifier:cellID];
        if(cell==nil)
        {
            cell=[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:cellID];
        }
        NSDictionary *dic=arrAutoLayout[indexPath.row];
        cell.textLabel.numberOfLines=0;
        cell.textLabel.lineBreakMode=NSLineBreakByCharWrapping;
        cell.textLabel.frame=CGRectMake(0, 0, tableView.frame.size.width, 40);
        cell.textLabel.text=[NSString stringWithFormat:@"%@(Priority:%ld)" ,dic[@"Type"],(long)[dic[@"Priority"] integerValue]];
        [cell.textLabel sizeToFit];
        cell.detailTextLabel.numberOfLines=0;
        cell.detailTextLabel.lineBreakMode=NSLineBreakByCharWrapping;
        cell.textLabel.frame=CGRectMake(0, 40, tableView.frame.size.width, 30);
        NSArray *arrTemp=[dic[@"Value"] componentsSeparatedByString:@" "];
        NSMutableArray *arr=[[NSMutableArray alloc] initWithCapacity:30];
        for(int i=1;i<arrTemp.count;i++)
        {
            [arr addObject:arrTemp[i]];
        }
        cell.detailTextLabel.text=[[arr componentsJoinedByString:@" "] stringByReplacingOccurrencesOfString:@">" withString:@""];
        [cell.detailTextLabel sizeToFit];
        return cell;
    }
    if(view==nil)
    {
        cell.textLabel.text=@"view has released";
        cell.detailTextLabel.text=@"";
    }
    cell.textLabel.text=view?NSStringFromClass([view class]):@"Remove FromSuperView";
    cell.detailTextLabel.text=[NSString stringWithFormat:@"l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf",view.frame.origin.x,view.frame.origin.y,view.frame.size.width,view.frame.size.height];
    if([view isKindOfClass:[UILabel class]] || [view isKindOfClass:[UITextField class]] || [view isKindOfClass:[UITextView class]])
    {
        cell.detailTextLabel.text=[cell.detailTextLabel.text stringByAppendingString:[NSString stringWithFormat:@" text(%ld):%@",(long)[[view valueForKey:@"text"] length],[view valueForKey:@"text"]]];
    }
    else if([view isKindOfClass:[UIButton class]])
    { 
        UIButton *btn=(UIButton*)view;
        NSString *str=[btn titleForState:UIControlStateNormal];
        cell.detailTextLabel.text=[cell.detailTextLabel.text stringByAppendingString:[NSString stringWithFormat:@" text(%ld):%@",(long)str.length,str!=nil?str:@"" ]];
    }
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==_tableSuper)
    {
        UIView *view=((RunTraceObject*)arrSuper[indexPath.row]).object;
        [[NSNotificationCenter defaultCenter] postNotificationName:msgRunTraceSuperView object:view];
        if(view && !view.translatesAutoresizingMaskIntoConstraints)
        {
            [self analysisAutoLayout:view];
        }
        else if(view==nil)
        {
            [tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
            return;
        }
    }
    else if(tableView==_tableSub)
    {
        UIView *view=((RunTraceObject*)arrSub[indexPath.row]).object;;
        [[NSNotificationCenter defaultCenter] postNotificationName:msgRunTraceSubView object:view];
        if(view && !view.translatesAutoresizingMaskIntoConstraints)
        {
            [self analysisAutoLayout:view];
        }
        else if(view==nil)
        {
            [tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
            return;
        }
    }
    else if(tableView==tableTrace)
    {
        return;
    }
    else if(tableView==tableAutoLayout)
    {
        NSString *strType=arrAutoLayout[indexPath.row][@"Type"];
        if([strType isEqualToString:@"IntrinsicContent Width"] || [strType isEqualToString:@"IntrinsicContent Height"] || [strType isEqualToString:@"BaseLine"])
        {
            return;
        }
        if(viewAutoLayout==nil)
        {
            [self onClose:nil];
            return;
        }
        NSMutableDictionary* dic=[NSMutableDictionary dictionaryWithDictionary:arrAutoLayout[indexPath.row]];
        [dic setObject:[RunTraceObject objectWithWeak:viewAutoLayout] forKey:@"View"];
        [[NSNotificationCenter defaultCenter] postNotificationName:msgRunTraceContraints object:dic];
    }
    originFrame=self.frame;
    CGRect frame=CGRectMake([UIScreen mainScreen].bounds.size.width-20,
                            [UIScreen mainScreen].bounds.size.height/2-20,
                            20,
                            40);
    [UIView animateWithDuration:0.2 animations:^{
        self.frame=frame;
    } completion:^(BOOL finished) {
        UIButton *btn=[[UIButton alloc] initWithFrame:self.bounds];
        [btn setTitle:@"<" forState:UIControlStateNormal];
        btn.backgroundColor=[UIColor blackColor];
        [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(expand:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:btn];
    }];
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==tableTrace || tableView==tableAutoLayout)
    {
        return 75;
    }
    else
    {
        return 44;
    }
}

-(BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==tableTrace || tableView==tableAutoLayout)
    {
        return NO;
    }
    return YES;
}

-(NSString*)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return @"Trace";
}

-(void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(editingStyle==UITableViewCellEditingStyleDelete)
    {
        [tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
        if(tableView==_tableSuper)
        {
            [self traceSuperAndSubView:((RunTraceObject*)arrSuper[indexPath.row]).object];
        }
        else if(tableView==_tableSub)
        {
            [self traceSuperAndSubView:((RunTraceObject*)arrSub[indexPath.row]).object];
        }
        
    }
}

-(void)traceSuperAndSubView:(UIView*)view
{
    if(view==nil)
    {
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"RunTrace" message:@"View has removed and can't trace!" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction * okAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:nil];
        [alert addAction:okAction];
        [self.window.rootViewController presentViewController:alert animated:YES completion:nil];
        return;
    }
    tableTrace=[[UITableView alloc] initWithFrame:CGRectMake(0, 50, 300, 250)];
    tableTrace.delegate=self;
    tableTrace.dataSource=self;
    [self addSubview:tableTrace];
    [_btnClose setTitle:@"Stop" forState:UIControlStateNormal];
    [_btnTrace setTitle:@">" forState:UIControlStateNormal];
    _lbTip.text=@"Trace Log:";
    [self bringSubviewToFront:_btnClose];
    [self bringSubviewToFront:_btnTrace];
    viewTrack=view;
    viewTrackBorderWidth=viewTrack.layer.borderWidth;
    ViewTrackBorderColor=[UIColor colorWithCGColor:viewTrack.layer.borderColor] ;
    viewTrack.layer.borderWidth=3;
    viewTrack.layer.borderColor=[UIColor blackColor].CGColor;
    [self addObserver];
    originFrame=self.frame;
    CGRect frame=CGRectMake([UIScreen mainScreen].bounds.size.width-20, [UIScreen mainScreen].bounds.size.height/2-20, 20, 40);
    [UIView animateWithDuration:0.2 animations:^{
        self.frame=frame;
    } completion:^(BOOL finished) {
        UIButton *btn=[[UIButton alloc] initWithFrame:self.bounds];
        [btn setTitle:@"<" forState:UIControlStateNormal];
        btn.backgroundColor=[UIColor blackColor];
        [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(expand:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:btn];
    }];
}

-(void)expand:(UIButton*)btn
{
    [btn removeFromSuperview];
    [UIView animateWithDuration:0.2 animations:^{
        self.frame=originFrame;
    }];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    bTouch=YES;
    UITouch *touch=[touches anyObject];
    CGPoint p=[touch locationInView:self];
    left=p.x;
    top=p.y;
}

-(void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    if(!bTouch)
    {
        return;
    }
    UITouch *touch=[touches anyObject];
    CGPoint p=[touch locationInView:self.window];
    self.frame=CGRectMake(p.x-left, p.y-top, self.frame.size.width, self.frame.size.height);
}

-(void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    bTouch=NO;
}

-(void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    bTouch=NO;
}

- (IBAction)onTrace:(id)sender
{
    if([[_btnTrace titleForState:UIControlStateNormal] isEqualToString:@"Trace"])
    {
        if(_viewHit==nil)
        {
            UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"RunTrace" message:@"View has removed and can't trace!" preferredStyle:UIAlertControllerStyleAlert];
            UIAlertAction * okAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:nil];
            [alert addAction:okAction];
            [self.window.rootViewController presentViewController:alert animated:YES completion:nil];
            return;
        }
        tableTrace=[[UITableView alloc] initWithFrame:CGRectMake(0, 50, 300, 250)];
        tableTrace.delegate=self;
        tableTrace.dataSource=self;
        [self addSubview:tableTrace];
        [_btnClose setTitle:@"Stop" forState:UIControlStateNormal];
        [_btnTrace setTitle:@">" forState:UIControlStateNormal];
        _btnAutoLayout.hidden=YES;
        _lbTip.text=@"Trace Log:";
        [self bringSubviewToFront:_btnClose];
        [self bringSubviewToFront:_btnTrace];
        viewTrack=_viewHit;
        viewTrackBorderWidth=viewTrack.layer.borderWidth;
        ViewTrackBorderColor=[UIColor colorWithCGColor:viewTrack.layer.borderColor ];
        viewTrack.layer.borderWidth=3;
        viewTrack.layer.borderColor=[UIColor blackColor].CGColor;
        [self addObserver];
        originFrame=self.frame;
        CGRect frame=CGRectMake([UIScreen mainScreen].bounds.size.width-20, [UIScreen mainScreen].bounds.size.height/2-20, 20, 40);
        [UIView animateWithDuration:0.2 animations:^{
            self.frame=frame;
        } completion:^(BOOL finished) {
            UIButton *btn=[[UIButton alloc] initWithFrame:self.bounds];
            [btn setTitle:@"<" forState:UIControlStateNormal];
            btn.backgroundColor=[UIColor blackColor];
            [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            [btn addTarget:self action:@selector(expand:) forControlEvents:UIControlEventTouchUpInside];
            [self addSubview:btn];
        }];
    }
    else
    {
        originFrame=self.frame;
        CGRect frame=CGRectMake([UIScreen mainScreen].bounds.size.width-20, [UIScreen mainScreen].bounds.size.height/2-20, 20, 40);
        [UIView animateWithDuration:0.2 animations:^{
            self.frame=frame;
        } completion:^(BOOL finished) {
            UIButton *btn=[[UIButton alloc] initWithFrame:self.bounds];
            [btn setTitle:@"<" forState:UIControlStateNormal];
            btn.backgroundColor=[UIColor blackColor];
            [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            [btn addTarget:self action:@selector(expand:) forControlEvents:UIControlEventTouchUpInside];
            [self addSubview:btn];
        }];
    }
}

- (IBAction)onDetail:(id)sender
{

}

-(void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    
    if([keyPath isEqualToString:@"frame"])
    {
        CGRect oldFrame=[change[NSKeyValueChangeOldKey] CGRectValue];
        CGRect newFrame=[change[NSKeyValueChangeNewKey] CGRectValue];
        if(CGRectEqualToRect(oldFrame, newFrame))
        {
            return;
        }
        [arrTrace addObject:@{
                             @"Key":@"Frame Change",
                             @"Time":[self currentDate],
                             @"OldValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",oldFrame.origin.x,oldFrame.origin.y,oldFrame.size.width,oldFrame.size.height],
                             @"NewValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",newFrame.origin.x,newFrame.origin.y,newFrame.size.width,newFrame.size.height]
                              }];
    }
    else if([keyPath isEqualToString:@"center"])
    {
        CGPoint oldCenter=[change[NSKeyValueChangeOldKey] CGPointValue];
        CGPoint newCenter=[change[NSKeyValueChangeNewKey] CGPointValue];
        if(CGPointEqualToPoint(oldCenter, newCenter))
        {
            return;
        }
        [arrTrace addObject:@{
                              @"Key":@"Center Change",
                              @"Time":[self currentDate],
                              @"OldValue":[NSString stringWithFormat:@"(x:%0.1lf y:%0.1lf)",oldCenter.x,oldCenter.y],
                              @"NewValue":[NSString stringWithFormat:@"(x:%0.1lf y:%0.1lf)",newCenter.x,newCenter.y]
                              }];
    }
    else if([keyPath isEqualToString:@"superview.frame"])
    {
        CGRect oldFrame=[change[NSKeyValueChangeOldKey] CGRectValue];
        CGRect newFrame=[change[NSKeyValueChangeNewKey] CGRectValue];
        if(CGRectEqualToRect(oldFrame, newFrame))
        {
            return;
        }
        [arrTrace addObject:@{
                              @"Key":@"Superview Frame Change",
                              @"Time":[self currentDate],
                              @"OldValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",oldFrame.origin.x,oldFrame.origin.y,oldFrame.size.width,oldFrame.size.height],
                              @"NewValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",newFrame.origin.x,newFrame.origin.y,newFrame.size.width,newFrame.size.height],
                              @"Superview":NSStringFromClass([((UIView*)object).superview class])
                              }];
    }
    else if([keyPath isEqualToString:@"tag"])
    {
        NSInteger oldVal=[change[NSKeyValueChangeOldKey] integerValue];
        NSInteger newVal=[change[NSKeyValueChangeNewKey] integerValue];
        [arrTrace addObject:@{
                              @"Key":@"Tag Change",
                              @"Time":[self currentDate],
                              @"OldValue":@(oldVal),
                              @"NewValue":@(newVal)
                              }];
    }
    else if([keyPath isEqualToString:@"userInteractionEnabled"])
    {
        BOOL oldVal=[change[NSKeyValueChangeOldKey] boolValue];
        BOOL newVal=[change[NSKeyValueChangeNewKey] boolValue];
        [arrTrace addObject:@{
                              @"Key":@"userInteractionEnabled Change",
                              @"Time":[self currentDate],
                              @"OldValue":oldVal?@"YES":@"NO",
                              @"NewValue":newVal?@"YES":@"NO"
                              }];
    }
    else if([keyPath isEqualToString:@"hidden"])
    {
        BOOL oldVal=[change[NSKeyValueChangeOldKey] boolValue];
        BOOL newVal=[change[NSKeyValueChangeNewKey] boolValue];
        [arrTrace addObject:@{
                              @"Key":@"hidden Change",
                              @"Time":[self currentDate],
                              @"OldValue":oldVal?@"YES":@"NO",
                              @"NewValue":newVal?@"YES":@"NO"
                              }];
    }
    else if([keyPath isEqualToString:@"bounds"])
    {
        CGRect oldFrame=[change[NSKeyValueChangeOldKey] CGRectValue];
        CGRect newFrame=[change[NSKeyValueChangeNewKey] CGRectValue];
        if(CGRectEqualToRect(oldFrame, newFrame))
        {
            return;
        }
        [arrTrace addObject:@{
                              @"Key":@"Bounds Change",
                              @"Time":[self currentDate],
                              @"OldValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",oldFrame.origin.x,oldFrame.origin.y,oldFrame.size.width,oldFrame.size.height],
                              @"NewValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf)",newFrame.origin.x,newFrame.origin.y,newFrame.size.width,newFrame.size.height]
                              }];
    }
    else if([keyPath isEqualToString:@"contentSize"])
    {
        CGSize oldSize=[change[NSKeyValueChangeOldKey] CGSizeValue];
        CGSize newSize=[change[NSKeyValueChangeNewKey] CGSizeValue];
        if(CGSizeEqualToSize(oldSize, newSize))
        {
            return;
        }
        [arrTrace addObject:@{
                              @"Key":@"ContentSize Change",
                              @"Time":[self currentDate],
                              @"OldValue":[NSString stringWithFormat:@"(w:%0.1lf h:%0.1lf)",oldSize.width,oldSize.height],
                              @"NewValue":[NSString stringWithFormat:@"(w:%0.1lf h:%0.1lf)",newSize.width,newSize.height]
                              }];
    }
    else if([keyPath isEqualToString:@"contentOffset"])
    {
        CGPoint oldOffset=[change[NSKeyValueChangeOldKey] CGPointValue];
        CGPoint newOffset=[change[NSKeyValueChangeNewKey] CGPointValue];
        if(CGPointEqualToPoint(oldOffset, newOffset))
        {
            return;
        }
        [arrTrace addObject:@{
                              @"Key":@"ContentOffset Change",
                              @"Time":[self currentDate],
                              @"OldValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf)",oldOffset.x,oldOffset.y],
                              @"NewValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf)",newOffset.x,newOffset.y]
                              }];
    }
    else if([keyPath isEqualToString:@"contentInset"])
    {
        UIEdgeInsets oldEdge=[change[NSKeyValueChangeOldKey] UIEdgeInsetsValue];
        UIEdgeInsets newEdge=[change[NSKeyValueChangeNewKey] UIEdgeInsetsValue];
        if(UIEdgeInsetsEqualToEdgeInsets(oldEdge, newEdge))
        {
            return;
        }
        [arrTrace addObject:@{
                              @"Key":@"ContentInset Change",
                              @"Time":[self currentDate],
                              @"OldValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf r:%0.1lf b:%0.1lf)",oldEdge.left,oldEdge.top,oldEdge.right,oldEdge.bottom],
                              @"NewValue":[NSString stringWithFormat:@"(l:%0.1lf t:%0.1lf r:%0.1lf b:%0.1lf)",newEdge.left,oldEdge.top,newEdge.right,oldEdge.bottom]
                              }];
    }
    [tableTrace reloadData];
    tableTrace.tableFooterView=[[UIView alloc] init];
}

- (NSString *)currentDate
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"HH:mm:ss:SSS"];
    NSString *destDateString = [dateFormatter stringFromDate:[NSDate date]];
    return destDateString;
}

-(void)addObserver
{
    [viewTrack addObserver:self forKeyPath:@"frame" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    [viewTrack addObserver:self forKeyPath:@"center" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    [viewTrack addObserver:self forKeyPath:@"superview.frame" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    [viewTrack addObserver:self forKeyPath:@"tag" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    [viewTrack addObserver:self forKeyPath:@"userInteractionEnabled" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    [viewTrack addObserver:self forKeyPath:@"hidden" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    [viewTrack addObserver:self forKeyPath:@"bounds" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    if([viewTrack isKindOfClass:[UIScrollView class]])
    {
        [viewTrack addObserver:self forKeyPath:@"contentSize" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
        [viewTrack addObserver:self forKeyPath:@"contentOffset" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
        [viewTrack addObserver:self forKeyPath:@"contentInset" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    }
}

-(void)removeObserver
{
    [viewTrack removeObserver:self forKeyPath:@"frame"];
    [viewTrack removeObserver:self forKeyPath:@"center"];
    [viewTrack removeObserver:self forKeyPath:@"superview.frame"];
    [viewTrack removeObserver:self forKeyPath:@"tag"];
    [viewTrack removeObserver:self forKeyPath:@"userInteractionEnabled"];
    [viewTrack removeObserver:self forKeyPath:@"hidden"];
    [viewTrack removeObserver:self forKeyPath:@"bounds"];
    if([viewTrack isKindOfClass:[UIScrollView class]])
    {
        [viewTrack removeObserver:self forKeyPath:@"contentSize"];
        [viewTrack removeObserver:self forKeyPath:@"contentOffset"];
        [viewTrack removeObserver:self forKeyPath:@"contentInset"];
    }
}

- (IBAction)onAutoLayout:(id)sender
{
    [self analysisAutoLayout:_viewHit];
}


-(void)analysisAutoLayout:(UIView*)view
{
    tableAutoLayout=[[UITableView alloc] initWithFrame:CGRectMake(0, 50, 300, 250)];
    tableAutoLayout.delegate=self;
    tableAutoLayout.dataSource=self;
    [self addSubview:tableAutoLayout];
    [_btnClose setTitle:@"Back" forState:UIControlStateNormal];
    _btnTrace.hidden=YES;
    _btnAutoLayout.hidden=YES;
    _lbTip.text=[NSString stringWithFormat:@"%@:%p",NSStringFromClass([view class]),(void*)view];
    viewAutoLayout=view;
    UIView *viewContraint=view;
    while(viewContraint!=nil && ![viewContraint isKindOfClass:NSClassFromString(@"UIViewControllerWrapperView")])
    {
        for(NSLayoutConstraint *con in viewContraint.constraints)
        {
            CGFloat constant=con.constant;
            UIView *viewFirst=con.firstItem;
            UIView *viewSecond=con.secondItem;
            if(con.secondItem!=nil )
            {
                if(con.firstItem==viewAutoLayout && con.firstAttribute==con.secondAttribute)
                {
                    if([viewFirst isDescendantOfView:viewSecond])
                    {
                        constant=con.constant;
                    }
                    else if([viewSecond isDescendantOfView:viewFirst])
                    {
                        constant=-con.constant;
                    }
                    else
                    {
                        constant=con.constant;
                    }
                }
                else if(con.firstItem==viewAutoLayout && con.firstAttribute!=con.secondAttribute)
                {
                    constant=con.constant;
                }
                else if(con.secondItem==viewAutoLayout && con.firstAttribute==con.secondAttribute)
                {
                    if([viewFirst isDescendantOfView:viewSecond])
                    {
                        constant=-con.constant;
                    }
                    else if([viewSecond isDescendantOfView:viewFirst])
                    {
                        constant=con.constant;
                    }
                    else
                    {
                        constant=con.constant;
                    }
                }
                else if(con.secondItem==viewAutoLayout && con.firstAttribute!=con.secondAttribute)
                {
                    constant=con.constant;
                }
            }
            
            if(con.firstItem==viewAutoLayout && (con.firstAttribute==NSLayoutAttributeLeading || con.firstAttribute==NSLayoutAttributeLeft || con.firstAttribute==NSLayoutAttributeLeadingMargin || con.firstAttribute==NSLayoutAttributeLeftMargin))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"Left",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.secondItem],
                                           @"Constant":@(con.constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.secondItem==viewAutoLayout && (con.secondAttribute==NSLayoutAttributeLeading || con.secondAttribute==NSLayoutAttributeLeft || con.secondAttribute==NSLayoutAttributeLeadingMargin || con.secondAttribute==NSLayoutAttributeLeftMargin))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"Left",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.firstItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.firstItem==viewAutoLayout && (con.firstAttribute==NSLayoutAttributeTop || con.firstAttribute==NSLayoutAttributeTopMargin))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"Top",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.secondItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.secondItem==viewAutoLayout && (con.secondAttribute==NSLayoutAttributeTop || con.secondAttribute==NSLayoutAttributeTopMargin))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"Top",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.firstItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.firstItem==viewAutoLayout && (con.firstAttribute==NSLayoutAttributeTrailing || con.firstAttribute==NSLayoutAttributeTrailingMargin || con.firstAttribute==NSLayoutAttributeRight || con.firstAttribute==NSLayoutAttributeRightMargin))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"Right",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.secondItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.secondItem==viewAutoLayout && (con.secondAttribute==NSLayoutAttributeTrailing || con.secondAttribute==NSLayoutAttributeTrailingMargin || con.secondAttribute==NSLayoutAttributeRight || con.secondAttribute==NSLayoutAttributeRightMargin))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"Right",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.firstItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.firstItem==viewAutoLayout && (con.firstAttribute==NSLayoutAttributeBottom || con.firstAttribute==NSLayoutAttributeBottomMargin))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"Bottom",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.secondItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.secondItem==viewAutoLayout && (con.secondAttribute==NSLayoutAttributeBottom || con.secondAttribute==NSLayoutAttributeBottomMargin))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"Bottom",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.firstItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if((con.firstItem==viewAutoLayout && con.firstAttribute==NSLayoutAttributeWidth) || (con.secondItem==viewAutoLayout && con.secondAttribute==NSLayoutAttributeWidth))
            {
                if([con isKindOfClass:NSClassFromString(@"NSContentSizeLayoutConstraint")])
                {
                    [arrAutoLayout addObject:@{
                                               @"Type":@"IntrinsicContent Width",
                                               @"Value":con.description,
                                               @"Constant":@(constant)
                                               }];
                }
                else
                {
                    [arrAutoLayout addObject:@{
                                               @"Type":@"Width",
                                               @"Value":con.description,
                                               @"ToView":[RunTraceObject objectWithWeak:con.firstItem==viewAutoLayout?con.secondItem:con.firstItem],
                                               @"Constant":@(constant),
                                               @"Multiplier":@(con.multiplier),
                                               @"Priority":@(con.priority)
                                               }];
                }
            }
            else if((con.firstItem==viewAutoLayout && con.firstAttribute==NSLayoutAttributeHeight) || (con.secondItem==viewAutoLayout && con.secondAttribute==NSLayoutAttributeHeight))
            {
                if([con isKindOfClass:NSClassFromString(@"NSContentSizeLayoutConstraint")])
                {
                    [arrAutoLayout addObject:@{
                                               @"Type":@"IntrinsicContent Height",
                                               @"Value":con.description,
                                               @"Constant":@(constant)
                                               }];
                }
                else
                {
                    [arrAutoLayout addObject:@{
                                               @"Type":@"Height",
                                               @"Value":con.description,
                                               @"ToView":[RunTraceObject objectWithWeak:con.firstItem==viewAutoLayout?con.secondItem:con.firstItem],
                                               @"Constant":@(constant),
                                               @"Multiplier":@(con.multiplier),
                                               @"Priority":@(con.priority)
                                               }];
                }
            }
            else if(con.firstItem==viewAutoLayout && (con.firstAttribute==NSLayoutAttributeCenterX))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"CenterX",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.secondItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.secondItem==viewAutoLayout && (con.secondAttribute==NSLayoutAttributeCenterX))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"CenterX",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.firstItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.firstItem==viewAutoLayout && (con.firstAttribute==NSLayoutAttributeCenterY))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"CenterY",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.secondItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.secondItem==viewAutoLayout && (con.secondAttribute==NSLayoutAttributeCenterY))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"CenterY",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.firstItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.firstItem==viewAutoLayout && (con.firstAttribute==NSLayoutAttributeBaseline))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"BaseLine",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.secondItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
            else if(con.secondItem==viewAutoLayout && (con.secondAttribute==NSLayoutAttributeBaseline))
            {
                [arrAutoLayout addObject:@{
                                           @"Type":@"BaseLine",
                                           @"Value":con.description,
                                           @"ToView":[RunTraceObject objectWithWeak:con.firstItem],
                                           @"Constant":@(constant),
                                           @"Multiplier":@(con.multiplier),
                                           @"Priority":@(con.priority)
                                           }];
            }
        }
        viewContraint=viewContraint.superview;
    }
    [tableAutoLayout reloadData];
    tableAutoLayout.tableFooterView=[[UIView alloc] init];
}

- (IBAction)onPosition:(id)sender
{
    if([[_btnPosition titleForState:UIControlStateNormal] isEqualToString:@"Top"])
    {
        [_btnPosition setTitle:@"Bottom" forState:UIControlStateNormal];
        [[NSNotificationCenter defaultCenter] postNotificationName:msgRunTraceInfoPosition object:[NSValue valueWithCGRect:CGRectMake(0, [UIScreen mainScreen].bounds.size.height-50, [UIScreen mainScreen].bounds.size.width, 50) ]];
    }
    else
    {
        [_btnPosition setTitle:@"Top" forState:UIControlStateNormal];
        [[NSNotificationCenter defaultCenter] postNotificationName:msgRunTraceInfoPosition object:[NSValue valueWithCGRect:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, 50) ]];
    }
}

- (IBAction)onDonate:(id)sender
{
    NSMutableString *s = [[NSMutableString alloc] initWithString:@"moc.qq@475414593:号账助捐宝付支"];
    NSMutableString *strResult=[NSMutableString string];
    for (NSUInteger i=s.length; i>0; i--) {
        [strResult appendString:[s substringWithRange:NSMakeRange(i-1, 1)]];
    }

    UIAlertController *alert = [UIAlertController alertControllerWithTitle:strResult message:@"非常感谢你使用本工具，如果你觉得本工具不错，请支持下我，如果您有任何建议，也请及时和我联系，如果有您的支持，将会是我把它做的更好的最大动力！" preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction * okAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:nil];
    [alert addAction:okAction];
    [self.window.rootViewController presentViewController:alert animated:YES completion:nil];
}
@end










