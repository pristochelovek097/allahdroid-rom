# allahdroid-rom
⚠️ДИСКЛЕЙМЕР: ТОЛЬКО ЮМОР/САТИРА! Эта прошивка - техническая шутка с чёрным юмором. Никакого отношения к религиям, верованиям и людям. Для взрослых гиков. Легко обижаемым - НЕ ИСПОЛЬЗОВАТЬ.


информация
==========

это прошивка allah, текущая версия 1.5-pre-release.

инструкция
==========

сборка:

```bash
repo init -u https://android.googlesource.com/platform/manifest -b android-5.1.1_r38
mkdir -p .repo/local_manifests
cd .repo/local_manifests
git clone https://github.com/pristochelovek097/allahdroid-rom.git
cp allahdroid-rom/local_manifests/allahdroid.xml ./
cd ../..
repo sync -j16

# сборка ядра goldfish
git clone https://github.com/pristochelovek097/allahdroid-kernel-goldfish -b android-goldfish-3.4 --depth 1
cd goldfish
export ARCH=arm
export CROSS_COMPILE=~/*ваш путь к андроиду*/prebuilts/gcc/linux-x86/arm/arm-linux-androideabi-4.8/bin/arm-linux-androideabi-
make goldfish_armv7_defconfig
make -j16
cd ..

source build/envsetup.sh
lunch allah_arm-userdebug
make -j16
```

запуск

```bash
# для первого запуска после сборки/пересборки
./scripts/start -launch-after-reassembly
# просто запуск
./scripts/start -launch
# запуск с флагами для emulator
./scripts/start -launch --emulator="флаги"
# запуск с интерактивной командной строкой в system.img
./scripts/start -ls-in-systemimg
# запуск с интерактивной командной строкой в userdata.img
./scripts/start -ls-in-dataimg
```

создание анимации загрузки

```bash
# создание просто бутанимки. стандартное разрешение 1920x1080
./scripts/bootanim -part0 video.mp4 -part1 video.mp4
# создание бутанимки со своим разрешением
./scripts/bootanim -part0 video.mp4 -part1 video.mp4 -weight *ширина* -height *высота*
```

----------
информация
----------

# что это?
## это прошивка allahdroid, юмористическая прошивка

# версия отсылает на нацизм?
## нет, мы просто много раз пересобирали и такое число у нас получилось.. простите.

# прошивка основана на LineageOS?
## нет, она создана полностью с AOSP

# почему вы так назвали прошивку?
## иза моего юмора
