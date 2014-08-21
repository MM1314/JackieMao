/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef ANDROID_SF_CLIENT_H
#define ANDROID_SF_CLIENT_H

#include <stdint.h>
#include <sys/types.h>

#include <utils/Errors.h>
#include <utils/KeyedVector.h>
#include <utils/Mutex.h>

#include <gui/ISurfaceComposerClient.h>

namespace android {

// ---------------------------------------------------------------------------

class LayerBaseClient;
class SurfaceFlinger;

// ---------------------------------------------------------------------------

class Client : public BnSurfaceComposerClient
{
public:
        Client(const sp<SurfaceFlinger>& flinger);
        ~Client();

    status_t initCheck() const;

    // protected by SurfaceFlinger::mStateLock
    size_t attachLayer(const sp<LayerBaseClient>& layer);

    void detachLayer(const LayerBaseClient* layer);

    sp<LayerBaseClient> getLayerUser(int32_t i) const;

private:
    // ISurfaceComposerClient interface
    virtual sp<ISurface> createSurface(
            surface_data_t* params, const String8& name,
            uint32_t w, uint32_t h,PixelFormat format,
            uint32_t flags);

    virtual status_t destroySurface(SurfaceID surfaceId);

    virtual status_t onTransact(
        uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags);

    // constant
   //主要的属性就是SurfaceFlinger
    sp<SurfaceFlinger> mFlinger;

    // protected by mLock
    DefaultKeyedVector< size_t, wp<LayerBaseClient> > mLayers;
    size_t mNameGenerator;

    // thread-safe
    mutable Mutex mLock;
};

// ---------------------------------------------------------------------------
}; // namespace android

#endif // ANDROID_SF_CLIENT_H