import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class BroadcastReceiverExample extends StatefulWidget {
  const BroadcastReceiverExample({super.key});

  @override
  State<BroadcastReceiverExample> createState() =>
      _BroadcastReceiverExampleState();
}

class _BroadcastReceiverExampleState extends State<BroadcastReceiverExample> {
  static const platform = MethodChannel('com.example.yourapp/broadcast');
  String _broadcastData = "No data received";

  Future<void> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case 'broadcastReceived':
        setState(() {
          _broadcastData = call.arguments;
        });
        break;
      default:
        throw MissingPluginException('notImplemented');
    }
  }

  Future<void> _sendBroadcast(String data) async {
    try {
      await platform.invokeMethod('sendBroadcast', {'data': data});
    } on PlatformException catch (e) {
      debugPrint("Failed to send broadcast: '${e.message}'.");
    }
  }

  @override
  void initState() {
    super.initState();
    platform.setMethodCallHandler(_handleMethod);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Broadcast Receiver Example'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text('Broadcast Data: $_broadcastData'),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () => _sendBroadcast("Hello from Dart!"),
              child: const Text('Send Broadcast'),
            ),
          ],
        ),
      ),
    );
  }
}
