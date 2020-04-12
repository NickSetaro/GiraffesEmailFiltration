import 'package:flutter/material.dart';
import 'bucket.dart';


class BucketList extends StatefulWidget {
  final List<Bucket> buckets;
  BucketList(this.buckets);

  @override
  _BucketListState createState() => _BucketListState();
}

class _BucketListState extends State<BucketList> {


  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      shrinkWrap: true,
      itemBuilder: (context, index) {
        return Container(
          child: Text(
            widget.buckets[index].name,
            style :TextStyle(
                fontWeight: FontWeight.w700,
                fontSize: 20
            ),
          ),



        );
      },
      itemCount: widget.buckets.length,
    );
  }
}