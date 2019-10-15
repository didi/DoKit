//
//  DoraemonTimeProfilerViewController.m
//  AFNetworking
//
//  Created by didi on 2019/10/15.
//

#import "DoraemonTimeProfilerViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonTimeProfilerViewController()

@property (nonatomic, strong) UILabel *contentLabel;

@end

@implementation DoraemonTimeProfilerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = DoraemonLocalizedString(@"函数耗时");
    
    NSString *contet = @"\n\n\n 函数不提供UI操作界面，在你需要分析的代码之前插入 \n\n [DoraemonTimeProfiler startRecord]; \n\n结束的地方加上 \n\n[DoraemonTimeProfiler stopRecord];\n\n 然后手动操作App执行代码流程，即可在控制台看到完整的函数耗时分析。\n\nsdk过滤了代码调用层次>10层，耗时小于1ms的函数调用，当然你也可以通过api自行设置。 \n\n 分析完毕之后，记得删掉startRecord和stopRecord的函数调用。";
    _contentLabel = [[UILabel alloc] init];
    _contentLabel.textColor = [UIColor doraemon_black_2];
    _contentLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
    _contentLabel.numberOfLines = 0;
    [self.view addSubview:_contentLabel];
    NSMutableAttributedString *attrStr = [[NSMutableAttributedString alloc] initWithString:contet];
    NSRange range = [contet rangeOfString:@"[DoraemonTimeProfiler startRecord];"];
    [attrStr addAttribute:NSForegroundColorAttributeName value:[UIColor redColor] range:range];
    range = [contet rangeOfString:@"[DoraemonTimeProfiler stopRecord];"];
    [attrStr addAttribute:NSForegroundColorAttributeName value:[UIColor redColor] range:range];
    range = [contet rangeOfString:@"分析完毕之后，记得删掉startRecord和stopRecord的函数调用。"];
    [attrStr addAttribute:NSForegroundColorAttributeName value:[UIColor redColor] range:range];
    _contentLabel.attributedText = attrStr;
    
    CGSize fontSize = [_contentLabel sizeThatFits:CGSizeMake(self.view.doraemon_width-40, MAXFLOAT)];
    _contentLabel.frame = CGRectMake(20, self.bigTitleView.doraemon_bottom, fontSize.width, fontSize.height);
}

- (BOOL)needBigTitleView{
    return YES;
}

@end
