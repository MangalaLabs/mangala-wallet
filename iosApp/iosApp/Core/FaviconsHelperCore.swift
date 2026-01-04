//
//  FaviconsHelper.swift
//  DuckDuckGo
//
//  Copyright © 2022 DuckDuckGo. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import Foundation
import Kingfisher

struct FaviconsHelperCore {

    // this function is now static and outside of Favicons, otherwise there is a circular dependency between
    // Favicons and NotFoundCachingDownloader
    public static func defaultResource(forDomain domain: String?, sourcesProvider: FaviconSourcesProvider) -> Kingfisher.ImageResource? {
        guard let domain = domain,
              let source = sourcesProvider.mainSource(forDomain: domain) else { return nil }

        let key = Favicons.createHash(ofDomain: domain)
        return Kingfisher.ImageResource(downloadURL: source, cacheKey: key)
    }
    
//    public static func defaultResource(forDomain domain: String?, sourcesProvider: FaviconSourcesProvider) -> ImageResource? {
//        guard let domain = domain else { return nil }
//        let key = Favicons.createHash(ofDomain: domain)
//        return ImageResource.resourceForDomain(domain: domain, cacheKey: key)
//    }
    
//    public static func defaultResource(forDomain domain: String?, sourcesProvider: FaviconSourcesProvider) -> ImageResource? {
//            guard let domain = domain else { return nil }
//            let key = Favicons.createHash(ofDomain: domain)
//            // Assuming the domain is a valid URL.
//            guard let url = URL(string: domain) else { return nil }
//            return ImageResource(downloadURL: url, cacheKey: key)
//        }
}

//extension ImageResource {
//    static func resourceForDomain(domain: String, cacheKey: String) -> ImageResource? {
//        // Here, we make some assumptions:
//        // 1. You're trying to use some domain-based image naming in the main bundle.
//        // 2. The domain (or a hash of it) is used as the image name.
//
//        // Use the domain (or a hash of it) as the image name.
//        let imageName = cacheKey // or hash(domain) or any appropriate transformation
//
//        // Return an ImageResource with the main app bundle.
//        return ImageResource(name: imageName, bundle: .main)
//    }
//}
