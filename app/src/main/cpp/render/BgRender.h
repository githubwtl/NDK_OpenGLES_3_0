//
// Created by ByteFlow on 2019/7/17.
//

#ifndef NDK_OPENGLES_3_0_BGRENDER_H
#define NDK_OPENGLES_3_0_BGRENDER_H
#include "stdint.h"
#include <GLES3/gl3.h>
#include <ImageDef.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

class BgRender
{
public:
	BgRender();
	~BgRender();

    void Init();

    int CreateGlesEnv();

    //void CreateProgramWithFeedback(const char *vShaderStr, const char *fShaderStr);

    void SetImageData(uint8_t *pData, int width, int height);

    void SetIntParams(int paramType, int param);

    void Draw();

    void DestroyGlesEnv();

    void UnInit();

    static BgRender* GetInstance()
	{
		if (m_Instance == nullptr)
		{
			m_Instance = new BgRender();
		}

		return m_Instance;
	}

    static void DestroyInstance()
	{
		if (m_Instance)
		{
			delete m_Instance;
			m_Instance = nullptr;
		}

	}

private:
	static BgRender *m_Instance;
	GLuint m_ImageTextureId;
	GLuint m_FboTextureId;
	GLuint m_FboId;
	GLuint m_VaoIds[1];
	GLuint m_VboIds[3];
	GLint m_SamplerLoc;
	GLint m_TexSizeLoc;
	NativeImage m_RenderImage;
	GLuint m_ProgramObj;
	GLuint m_VertexShader;
	GLuint m_FragmentShader;

	EGLConfig  m_eglConf;
	EGLSurface m_eglSurface;
	EGLContext m_eglCtx;
	EGLDisplay m_eglDisplay;
	bool       m_IsGLContextReady;
	const char*m_fShaderStrs[5];
	int        m_ShaderIndex;
};


#endif //NDK_OPENGLES_3_0_BGRENDER_H
