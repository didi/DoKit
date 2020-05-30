# Make sure to run `pod lib lint DoraemonKit.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'DoraemonKit-Swift'
  s.version          = '0.0.1'
  s.summary          = 'iOS各式各样的工具集合'
  s.description      = <<-DESC
                          iOS各式各样的工具集合 Desc
                       DESC

  s.homepage         = 'https://github.com/didi/DoraemonKit'
  s.license          = { :type => 'Apache-2.0', :file => 'LICENSE' }
  s.author           = { 'yixiang' => 'javer_yi@163.com' }
  s.source           = { :git => 'https://github.com/didi/DoraemonKit.git', :tag => s.version.to_s }
  s.ios.deployment_target = '9.0'
  
  s.swift_versions = ['5', '5.1', '5.2']


  s.default_subspec = 'Core'
  
  s.subspec 'Core' do |ss| 
    ss.source_files = 'iOS/Swift/DoKitSwift/Src/**/*.{h,m,swift}'
    ss.resource_bundles = {
      'DoKitSwift' => 'iOS/DoraemonKit/Resource/**/*'
    }
  end
end

