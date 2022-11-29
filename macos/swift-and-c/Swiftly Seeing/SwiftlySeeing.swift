import SwiftUI

@main
struct SwiftlySeeing: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct IndigoButton: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
                .padding()
                .background(.indigo)
                .foregroundColor(.white)
                .clipShape(RoundedRectangle(cornerRadius: 5))
                .scaleEffect(configuration.isPressed ? 0.95 : 1)
    }
}

struct ContentView: View {

    var onButtonClick: () -> Void = {
        create_file(UnsafeMutablePointer<CChar>(mutating: NSString("test.txt").utf8String))
    }

    var body: some View {
        VStack {
            Text("Learning Swift")
                    .font(.system(size: 30))
                    .foregroundColor(.white)
            Button("call C fn", action: onButtonClick)
                    .buttonStyle(IndigoButton())
        }
                .padding()
                .background(.mint)
                .clipShape(RoundedRectangle(cornerRadius: 5))
                .frame(width: 200)
    }
}

#if DEBUG
struct ContentViewPreviews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
#endif
