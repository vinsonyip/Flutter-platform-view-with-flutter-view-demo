import 'dart:io';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Platform View'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const MethodChannel _methodChannel =
  MethodChannel('samples.flutter.io/platform_view');
  int platformCounter = 0; // Check if this is the init page
  int _counter = 0;
  bool isInit = false;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  Future<void> _launchPlatformCount() async {
    try{
      platformCounter =
      await _methodChannel.invokeMethod('switchView', _counter);

      setState((){
        _counter = platformCounter;
      });
    }on Exception{
      setState((){
        isInit = true;
      });
    }finally{
      print('------ isInit = $isInit ------');
    }
  }
  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    if(platformCounter == null){
      print('========= Init Page $isInit=========');
    }
    _launchPlatformCount();
  }


  @override
  Widget build(BuildContext context){

    return isInit? const FlutterViewPage(title: 'Flutter View Page',) : Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Expanded(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Text(
                    'Button tapped $_counter time${ _counter == 1 ? '' : 's' }.',
                    style: const TextStyle(fontSize: 17.0),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(18.0),
                    child: ElevatedButton(
                      onPressed: _launchPlatformCount,
                      child: Platform.isIOS
                          ? const Text('Continue in iOS view')
                          : const Text('Continue in Android view'),
                    ),
                  ),
                ],
              ),
            ),
          ),
          Container(
            padding: const EdgeInsets.only(bottom: 15.0, left: 5.0),
            child: Row(
              children: const <Widget>[
                Text(
                  'Flutter',
                  style: TextStyle(fontSize: 30.0),
                ),
              ],
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}


class FlutterViewPage extends StatefulWidget {
  const FlutterViewPage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<FlutterViewPage> createState() => _FlutterViewPageState();
}

class _FlutterViewPageState extends State<FlutterViewPage> {
  int _counter = 0;

  static const String _channel = 'increment';
  static const String _pong = 'pong';
  static const String _emptyMessage = '';
  static const BasicMessageChannel<String?> platform =
  BasicMessageChannel<String?>(_channel, StringCodec());

  Future<String> _handlePlatformIncrement(String? message) async {
    setState(() {
      _counter++;
    });
    return _emptyMessage;
  }

  void _sendFlutterIncrement() {
    platform.send(_pong);
  }

  @override
  void initState() {
    super.initState();
    platform.setMessageHandler(_handlePlatformIncrement);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: (){
          _sendFlutterIncrement();
        },
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
