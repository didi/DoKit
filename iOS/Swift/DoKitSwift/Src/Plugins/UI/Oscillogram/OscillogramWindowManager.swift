//
//  OscillogramWindowManager.swift
//  DoraemonKit-Swift
//
//  Created by hash0xd on 2020/6/30.
//

import Foundation

class OscillogramWindowManager {
    static let shared = OscillogramWindowManager()
    
    private  lazy var stackView: UIStackView = {
        let statckView = UIStackView()
        statckView.alignment = .fill
        statckView.axis = .vertical
        statckView.distribution = .equalSpacing
        statckView.spacing = 10
        return statckView
    }()
    
    private let window = OscillogramWindow(frame: CGRect(x: 0, y: 0, width: kScreenWidth, height: kScreenHeight))

    private init () {
        window.addSubview(stackView)
        stackView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: window.topAnchor, constant: 100),
            stackView.leadingAnchor.constraint(equalTo: window.leadingAnchor),
            stackView.trailingAnchor.constraint(equalTo: window.trailingAnchor),
        ])
    }
    
    func add(oscillogramView: OscillogramView) {
        guard !stackView.arrangedSubviews.contains(window) else {
            return
        }
        defer {
            window.isHidden = stackView.arrangedSubviews.isEmpty
        }
        oscillogramView.heightAnchor.constraint(equalToConstant: kSizeFrom750_Landscape(240)).isActive = true
        stackView.addArrangedSubview(oscillogramView)
        oscillogramView.start()
    }
    
    func remove(oscillogramView: OscillogramView) {
        defer {
            window.isHidden = stackView.arrangedSubviews.isEmpty
        }
        stackView.removeArrangedSubview(oscillogramView)
    }
}
