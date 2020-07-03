//
//  MemoryViewController.swift
//  DoraemonKit-Swift
//
//  Created by hash0xd on 2020/6/24.
//

import UIKit

class MemoryViewController: BaseViewController {
    
    static let oscillogramView = OscillogramView()
    private let switchView = CellSwitch(frame: .zero)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        set(title: LocalizedString("内存检测"))
        
        switchView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(switchView)
            
        if #available(iOS 11.0, *) {
            switchView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor).isActive = true
        } else {
            switchView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        }
        let switchViewConstraints = [
            switchView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            switchView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            switchView.heightAnchor.constraint(equalToConstant: CellSwitch.defaultHeight)
        ]
        NSLayoutConstraint.activate(switchViewConstraints)
        
        switchView.renderUIWithTitle(title: LocalizedString("内存检测开关"), on: MemoryPlugin.isOn)
        switchView.needTopLine()
        switchView.needDownLine()
        switchView.delegate = self
    }
}

extension MemoryViewController: CellSwitchDelegate {
    func changeSwitchOn(on: Bool) {
         MemoryPlugin.isOn = on
        if on {
            OscillogramWindowManager.shared.add(oscillogramView: MemoryViewController.oscillogramView)
            MemoryViewController.oscillogramView.delegate = self
        } else {
            OscillogramWindowManager.shared.remove(oscillogramView: MemoryViewController.oscillogramView)
        }
    }
}

extension MemoryViewController: OscillogramViewDelegate {
    var oscillogramMaxValue: Double {
        return MemoryCalculator.total
    }
    
    func collectData() -> Double {
        MemoryCalculator.memoryUsage()
    }
    
    func oscillogramViewDidColsed() {
        MemoryPlugin.isOn = false
        OscillogramWindowManager.shared.remove(oscillogramView: MemoryViewController.oscillogramView)
        switchView.renderUIWithTitle(title: LocalizedString("内存检测开关"), on:  MemoryPlugin.isOn)
    }

}
