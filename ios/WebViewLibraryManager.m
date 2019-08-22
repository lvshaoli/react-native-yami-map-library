//
//  WebViewLibraryManager.m
//  RNYamiMapLibrary
//
//  Created by 亚米 on 2019/8/22.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "WebViewLibraryManager.h"
#import "YJViewController.h"

@implementation WebViewLibraryManager
RCT_EXPORT_MODULE()
- (UIView *)view
{
    YJViewController *vc = [[YJViewController alloc] init];
    return vc.view;
}


@end
