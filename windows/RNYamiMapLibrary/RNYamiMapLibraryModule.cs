using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Yami.Map.Library.RNYamiMapLibrary
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNYamiMapLibraryModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNYamiMapLibraryModule"/>.
        /// </summary>
        internal RNYamiMapLibraryModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNYamiMapLibrary";
            }
        }
    }
}
