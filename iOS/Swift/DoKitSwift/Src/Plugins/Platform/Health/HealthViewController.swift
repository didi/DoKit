//
//  HealthViewController.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthViewController: BaseViewController {
    var scrollView: UIScrollView!
    var homeView: HealthHomeView!
    var instructionsView = HealthInstructionsView()
    var footerView = HealthFooterView()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.setTitle(title: LocalizedString("健康体检"))
        
        let offset_y = self.bigTitleView!.bottom
        homeView = HealthHomeView(frame: CGRect(x: 0, y: 0, width: view.bounds.width, height: view.height - offset_y))
        
        
        scrollView = UIScrollView(frame: CGRect(x: 0, y: offset_y, width: view.width, height: view.height - offset_y))
        scrollView.backgroundColor = .white
        scrollView.delegate = self
        scrollView.contentSize = CGSize(width: homeView.width, height: homeView.height * 2)//设置大小
        scrollView.isPagingEnabled = true
        
        scrollView.addSubview(homeView)
        view.addSubview(scrollView)
    }
    
    override func needBigTitleView() -> Bool {
        return true
    }
}

extension HealthViewController: UIScrollViewDelegate {
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let scrollToScrollStop = !scrollView.isTracking && !scrollView.isDragging && !scrollView.isDecelerating
        if (scrollToScrollStop) {
//            footerView.renderUIWithTitleImg(scrollView.contentOffset.y <= 0)
            footerView.isHidden = false
        }
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        footerView.isHidden = true
    }
}
