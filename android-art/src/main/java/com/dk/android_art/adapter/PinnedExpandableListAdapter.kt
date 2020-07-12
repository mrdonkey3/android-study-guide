package com.dk.android_art.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dk.android_art.R
import com.dk.android_art.entity.Group
import com.dk.android_art.entity.People

/**
 * @create on 2020/7/12 13:57
 * @description TODO
 * @author mrdonkey
 */
class PinnedExpandableListAdapter(
    val ctz: Context,
    val groupList: List<Group>,
    val childList: List<List<People>>
) : BaseExpandableListAdapter() {

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(ctz)
    }

    override fun getGroup(groupPosition: Int) = groupList.get(groupPosition)

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun hasStableIds() = true//item是否有唯一的id

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val holder = GroupViewHolder.getInstance(inflater)
        holder.tv.text = getGroup(groupPosition).title
        holder.iv.setImageResource(if (isExpanded) R.drawable.expanded else R.drawable.collapse)
        return holder.cView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val holder = ChildVieHolder.getInstance(inflater)
        val child = getChild(groupPosition, childPosition)
        holder.tvName.text = child.name
        holder.tvAddress.text = child.address
        holder.tvAge.text = child.age.toString()
        holder.btn.setOnClickListener {
            Toast.makeText(ctz, "clicked pos $groupPosition-$childPosition", Toast.LENGTH_SHORT)
                .show()
        }
        return holder.cView
    }

    override fun getChildrenCount(groupPosition: Int) = childList[groupPosition].size

    override fun getChild(groupPosition: Int, childPosition: Int) =
        childList[groupPosition][childPosition]

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()


    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun getGroupCount() = groupList.size

    companion object {
        class GroupViewHolder {
            lateinit var tv: TextView
            lateinit var iv: ImageView
            lateinit var cView: View

            companion object {
                private var INSTANCE: GroupViewHolder? = null
                fun getInstance(inflater: LayoutInflater): GroupViewHolder {
                    return INSTANCE ?: synchronized(this) {
                        val convertView = inflater.inflate(R.layout.group, null)
                        GroupViewHolder().apply {
                            cView = convertView
                            tv = convertView.findViewById(R.id.group)
                            iv = convertView.findViewById(R.id.image)
                        }
                    }
                }
            }
        }

        class ChildVieHolder {
            lateinit var tvName: TextView
            lateinit var tvAge: TextView
            lateinit var tvAddress: TextView
            lateinit var iv: ImageView
            lateinit var btn: Button
            lateinit var cView: View

            companion object {
                private var INSTANCE: ChildVieHolder? = null

                fun getInstance(inflater: LayoutInflater): ChildVieHolder {
                    return INSTANCE ?: synchronized(this) {
                        val convertView = inflater.inflate(R.layout.child, null)
                        ChildVieHolder().apply {
                            cView = convertView
                            tvName = convertView.findViewById(R.id.name)
                            tvAge = convertView.findViewById(R.id.age)
                            tvAddress = convertView.findViewById(R.id.address)
                            iv = convertView.findViewById(R.id.image)
                            btn = convertView.findViewById(R.id.button1)
                        }
                    }
                }
            }
        }
    }
}