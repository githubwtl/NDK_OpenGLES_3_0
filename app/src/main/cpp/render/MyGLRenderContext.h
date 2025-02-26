//
// Created by ByteFlow on 2019/7/9.
//

#ifndef NDK_OPENGLES_3_0_MYGLRENDERCONTEXT_H
#define NDK_OPENGLES_3_0_MYGLRENDERCONTEXT_H

#include "stdint.h"
#include <GLES3/gl3.h>
#include "TextureMapSample.h"
#include "NV21TextureMapSample.h"
#include "TriangleSample.h"

class MyGLRenderContext
{
	MyGLRenderContext();

	~MyGLRenderContext();

public:
	void SetImageData(int format, int width, int height, uint8_t *pData);

	void SetParamsInt(int paramType, int value0, int value1);

	void OnSurfaceCreated();

	void OnSurfaceChanged(int width, int height);

	void OnDrawFrame();

	static MyGLRenderContext* GetInstance();
	static void DestroyInstance();

private:
	static MyGLRenderContext *m_pContext;
	GLSampleBase *m_Sample;
	int m_ScreenW;
	int m_ScreenH;

};


#endif //NDK_OPENGLES_3_0_MYGLRENDERCONTEXT_H
