# todo if linux: ./src/build/install-build-deps.sh

import os
import shutil
import subprocess
import sys

uri_depot_tools_src = "https://chromium.googlesource.com/chromium/tools/depot_tools.git"
valid_cpus = ['x64', 'x86']

build = False
cpu = 'x64'
debug = True
revision = ''
update = False

install_path = os.path.join(os.path.abspath(os.getcwd()), 'libwebrtc')
depot_tools_path = os.path.join(install_path, 'depot_tools')
fetch_path = os.path.join(depot_tools_path, 'fetch' if not sys.platform == 'win32' else 'fetch.bat')
gclient_path = os.path.join(depot_tools_path, 'gclient' if not sys.platform == 'win32' else 'gclient.bat')
webrtc_path = os.path.join(install_path, 'webrtc')
webrtc_src_path = os.path.join(install_path, 'webrtc', 'src')
webrtc_out_path = os.path.join(webrtc_src_path, 'out')


def list_builds():
    no_builds = True
    valid_builds = ['debug', 'release']
    if os.path.isdir(webrtc_out_path):
        hash_dirs = [f for f in os.listdir(webrtc_out_path)
                     if os.path.isdir(os.path.join(webrtc_out_path, f)) and not f.endswith('latest')]
        for hash_dir in hash_dirs:
            print(f' - {hash_dir}')
            if not os.path.isdir(os.path.join(webrtc_out_path, hash_dir, 'headers')):
                print('   (warning: missing headers)')
            cpu_dirs = [f for f in os.listdir(os.path.join(webrtc_out_path, hash_dir))
                        if os.path.isdir(os.path.join(webrtc_out_path, hash_dir, f)) and f in valid_cpus]
            if len(cpu_dirs) != 0:
                no_builds = False
            for cpu_dir in cpu_dirs:
                print(f'   - {cpu_dir}')
                build_dirs = [f for f in os.listdir(os.path.join(webrtc_out_path, hash_dir, cpu_dir))
                              if os.path.isdir(os.path.join(webrtc_out_path, hash_dir, cpu_dir, f))
                              and f in valid_builds]
                for build_dir in build_dirs:
                    print(f'     - {build_dir}')
    if no_builds:
        print('no libwebrtc builds')
    exit(0)


for i, arg in enumerate(sys.argv):
    short_hand = arg.startswith('-') and not arg.startswith('--')
    if arg.startswith('--update') or (short_hand and 'u' in arg):
        update = True
    if arg.startswith('--update='):
        revision = arg[9:]
        if len(revision) != 10:
            print('\n--update must be a valid short git commit hash')
    if arg.startswith('--cpu='):
        cpu = arg[6:]
        if cpu not in valid_cpus:
            print('\n--cpu must be x86 or x64')
            exit(1)
    if arg == '--build' or (short_hand and 'b' in arg):
        build = True
    if arg == '--release' or (short_hand and 'r' in arg):
        debug = False
        build = True
    if arg == '--list' or (short_hand and 'l' in arg):
        list_builds()

path_sep = ';' if sys.platform == 'win32' else ':'
os.environ['PATH'] = depot_tools_path + path_sep + os.environ['PATH']
if sys.platform == 'win32':
    os.environ['DEPOT_TOOLS_WIN_TOOLCHAIN'] = '0'

if not os.path.isdir(install_path):
    os.mkdir(install_path)

if not os.path.isdir(depot_tools_path):
    print('~~~ installing depot tools ')
    subprocess.call(["git", "clone", uri_depot_tools_src], cwd=install_path)
else:
    print('~~~ updating depot tools ')
    subprocess.call(['git', 'pull', 'origin', 'main'], cwd=depot_tools_path)

if not os.path.isdir(webrtc_path):
    os.mkdir(webrtc_path)

if not os.path.isdir(webrtc_src_path):
    print('~~~ installing webrtc src ')
    subprocess.call([gclient_path, 'config', 'https://webrtc.googlesource.com/src.git'], cwd=webrtc_path)
    subprocess.call([gclient_path, 'sync'], cwd=webrtc_path)

if update:
    if len(revision) > 0:
        print('~~~ syncing webrtc to ' + revision)
        subprocess.call([gclient_path, 'sync', '--revision', 'src@' + revision], cwd=webrtc_path)
    else:
        print('~~~ updating webrtc ')
        subprocess.call(['git', 'checkout', 'main'], cwd=webrtc_src_path)
        subprocess.call(['git', 'pull'], cwd=webrtc_src_path)
        subprocess.call([gclient_path, 'sync'], cwd=webrtc_path)

if build:
    git_hash = subprocess.check_output(['git', 'rev-parse', '--short', 'HEAD'], cwd=webrtc_src_path) \
        .decode('utf-8').strip('\n')
    build_label = 'debug' if debug else 'release'
    build_path = os.path.join('out', git_hash, cpu, build_label)
    build_abs_path = os.path.join(webrtc_src_path, build_path)
    gn_path = os.path.join(depot_tools_path, 'gn' if not sys.platform == 'win32' else 'gn.bat')
    ninja_path = os.path.join(depot_tools_path, 'ninja' if not sys.platform == 'win32' else 'ninja.exe')
    print('~~~ building ' + git_hash + '/' + build_label)
    gn_gen_args = [
        'is_debug=true' if debug else 'is_debug=false',
        'enable_dsyms=true' if debug else 'enable_dsyms=false',
        'rtc_build_examples=false',
        'rtc_build_tools=false',
        'rtc_include_tests=false',
        f'target_cpu=\\"{cpu}\\"',
    ]
    if sys.platform == 'darwin':
        gn_gen_args.append('target_os=\\"mac\\"')
        gn_gen_args.append('is_clang=true')
        gn_gen_args.append('use_custom_libcxx=false')
        gn_gen_args.append('enable_iterator_debugging=false')
    if sys.platform == 'win32':
        gn_gen_args.append('is_clang=false')
        if debug:
            gn_gen_args.append('enable_iterator_debugging=true')
    print('~~~ gn gen ' + build_path)
    gn_gen_args = '--args=\"' + ' '.join(gn_gen_args) + '\"'
    # todo use subprocess.check_output and print instructions for platform-specific setup errors
    if 0 != subprocess.call(' '.join([gn_path, 'gen', gn_gen_args, build_path]), cwd=webrtc_src_path, shell=True):
        print('\n~~~ gn gen failed¡')
        exit(1)
    print('~~~ ninja -C ' + build_path)
    if 0 != subprocess.call([ninja_path, '-C', build_path], cwd=webrtc_src_path):
        print('\n~~~ ninja compile failed¡')
        exit(1)
    headers_path = os.path.join(webrtc_out_path, git_hash, 'headers')
    if not os.path.isdir(headers_path):
        print('~~~ copying headers to ' + headers_path)
        src_dirs = [
            'api',
            'audio',
            'base',
            'call',
            'common_audio',
            'common_video',
            'logging',
            'media',
            'modules',
            'p2p',
            'pc',
            'rtc_base',
            'system_wrappers',
            'video',
        ]


        def copy_header_dir(src_path, dest_path):
            for src_root, dirs, files in os.walk(src_path):
                dest_root = os.path.join(dest_path, os.path.relpath(src_root, src_path))
                for file in files:
                    src_file = os.path.join(src_root, file)
                    if os.path.isfile(src_file) and file[-2:].lower() == '.h':
                        os.makedirs(dest_root, exist_ok=True)
                        shutil.copy2(src_file, os.path.join(dest_root, file))


        for src_dir in src_dirs:
            copy_header_dir(os.path.join(webrtc_src_path, src_dir), os.path.join(headers_path, 'webrtc', src_dir))
        abseil_src_dir = os.path.join(webrtc_src_path, 'third_party', 'abseil-cpp', 'absl')
        copy_header_dir(abseil_src_dir, os.path.join(headers_path, 'absl'))
    latest_build_dir = os.path.join(webrtc_out_path, git_hash)
    latest_build_link = os.path.join(webrtc_out_path, 'latest')
    print('~~~ linking webrtc/src/out/' + git_hash + ' to webrtc/src/out/latest')
    if os.path.isdir(latest_build_link):
        os.remove(latest_build_link)
    if sys.platform == 'win32':
        os.system(f'mklink /d {latest_build_link} {latest_build_dir}')
    else:
        subprocess.call(['ln', '-Ffs', latest_build_dir, latest_build_link])
    print(f'\n~~~ build finished successfully!\n\n    set WEBRTC_CMT in CMakeLists.txt to {git_hash}\n')
