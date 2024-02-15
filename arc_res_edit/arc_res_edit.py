import os
import shutil

file_ends = {
    ".jpg": "res/blank.jpg",
    ".jpeg": "res/blank.jpg",
    ".png": "res/blank.png",
    ".ogg": "res/silence.ogg",
    ".wav": "res/silence.wav",
    ".mp4": "res/black_video.mp4"
}


def main():
    apk_root = input("apk 根目录位置: ")
    skip_paths = [
        # "assets/img",
        # os.path.normpath(os.path.join(apk_root, "assets/layouts")),
        os.path.normpath(os.path.join(apk_root, "assets/layouts/dialog")),
        os.path.normpath(os.path.join(apk_root, "assets/layouts/ingame")),
        os.path.normpath(os.path.join(apk_root, "assets/layouts/mainmenu")),
        os.path.normpath(os.path.join(apk_root, "assets/layouts/topbar")),
        os.path.normpath(os.path.join(apk_root, "assets/layouts/1080/ingame")),
        os.path.normpath(os.path.join(apk_root, "assets/layouts/1080/mainmenu")),
        os.path.normpath(os.path.join(apk_root, "assets/layouts/1080/network")),
        os.path.normpath(os.path.join(apk_root, "assets/layouts/1080/topbar")),
        os.path.normpath(os.path.join(apk_root, "assets/img/account")),
        os.path.normpath(os.path.join(apk_root, "assets/img/clear_type")),
        os.path.normpath(os.path.join(apk_root, "assets/img/course/banner")),
        os.path.normpath(os.path.join(apk_root, "assets/img/dialog_v2")),
        os.path.normpath(os.path.join(apk_root, "assets/img/ranking")),
        os.path.normpath(os.path.join(apk_root, "assets/img/scenery")),
        os.path.normpath(os.path.join(apk_root, "assets/startup/epilogue")),
    ]
    skip_files = [
        os.path.join(apk_root, "assets/img/activity_icon.png"),
        os.path.join(apk_root, "assets/img/rating_0.png"),
        os.path.join(apk_root, "assets/img/rating_1.png"),
        os.path.join(apk_root, "assets/img/rating_2.png"),
        os.path.join(apk_root, "assets/img/rating_3.png"),
        os.path.join(apk_root, "assets/img/rating_4.png"),
        os.path.join(apk_root, "assets/img/rating_5.png"),
        os.path.join(apk_root, "assets/img/rating_6.png"),
        os.path.join(apk_root, "assets/img/rating_7.png"),
        os.path.join(apk_root, "assets/img/rating_off.png"),
        os.path.join(apk_root, "assets/img/shutter_l.png"),
        os.path.join(apk_root, "assets/img/shutter_r.png"),
        os.path.join(apk_root, "assets/startup/start_icon.png"),
        os.path.join(apk_root, "assets/startup/start_wreath.png"),
        os.path.join(apk_root, "assets/startup/f1.png"),
        os.path.join(apk_root, "assets/startup/f2.png"),
        os.path.join(apk_root, "assets/startup/f3.png"),
        os.path.join(apk_root, "assets/startup/m1.png"),
        os.path.join(apk_root, "assets/startup/m2.png"),
        os.path.join(apk_root, "assets/startup/m3.png"),
        os.path.join(apk_root, "assets/startup/1080/bg.jpg"),
        os.path.join(apk_root, "assets/startup/1080/title.png"),
        os.path.join(apk_root, "assets/startup/1080/title_glow.png"),
    ]

    for root, dirs, files in os.walk(os.path.join(apk_root, "assets")):
        skip = False
        for i in skip_paths:
            if os.path.normpath(root).startswith(i):
                skip = True
                break
        if skip:
            continue
        for file in files:
            for k in file_ends:
                if file.endswith(k):
                    file_name = os.path.join(root, file)
                    skip_file = False
                    for i in skip_files:
                        if os.path.samefile(i, file_name):
                            skip_file = True
                            break
                    if skip_file:
                        continue
                    shutil.copyfile(file_ends[k], file_name)
                    print(f"replaced {file_name}")
    shutil.copyfile("res/chieri.png", os.path.join(apk_root, "res/drawable"))


if __name__ == "__main__":
    main()
