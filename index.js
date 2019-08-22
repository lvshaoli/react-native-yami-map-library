/**
 * @Author: lvshaoli
 * @DATE: 2019-08-20
 * @TIME: 11:11
 * @DESC: ''
 * */

import { requireNativeComponent, NativeModules, Platform } from 'react-native';

let RNWebViewLibraryManager;

if (Platform.OS === 'ios') {
    RNWebViewLibraryManager = requireNativeComponent('WebViewLibrary');
} else {
    RNWebViewLibraryManager = requireNativeComponent('RNWebViewLibraryManager');
}

export default RNWebViewLibraryManager;
