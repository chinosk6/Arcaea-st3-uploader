from pydub import AudioSegment
from PIL import Image
from moviepy.editor import VideoClip
import numpy as np


def make_black_frame(width, height):
    return np.zeros((height, width, 3), dtype=np.uint8)


def make_black_video(duration, fps, width, height):
    clip = VideoClip(lambda t: make_black_frame(width, height), duration=duration)
    clip = clip.set_fps(fps)
    return clip


def generate_res():
    # 创建0.5秒的空白声音
    silence = AudioSegment.silent(duration=500)
    silence.export("res/silence.wav", format="wav")
    silence.export("res/silence.ogg", format="ogg")

    # 创建空白图片
    pt = Image.new("RGBA", (10, 10), (255, 255, 255, 255))
    pt.save("res/blank.png")
    pt.convert("RGB").save("res/blank.jpg")

    # 视频参数
    duration = 0.5  # 视频时长（秒）
    fps = 25  # 每秒帧数
    width = 640  # 视频宽度
    height = 480  # 视频高度
    # 生成纯黑视频
    black_video = make_black_video(duration, fps, width, height)
    black_video.write_videofile("res/black_video.mp4", codec="libx264", fps=fps)


if __name__ == "__main__":
    generate_res()
