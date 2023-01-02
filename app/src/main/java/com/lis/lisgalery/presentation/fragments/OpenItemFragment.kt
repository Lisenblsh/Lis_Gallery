package com.lis.lisgalery.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.lis.domain.tools.ImageFun
import com.lis.lisgalery.databinding.FragmentOpenItemBinding

class OpenItemFragment : Fragment() {

    private lateinit var binding: FragmentOpenItemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpenItemBinding.inflate(inflater, container, false)
        val args = OpenItemFragmentArgs.fromBundle(requireArguments())
        if (args.isVideo) {
            binding.showVideo(args.path)
        } else {
            binding.showImageView(args.path)
        }
        return binding.root
    }

    private fun FragmentOpenItemBinding.showVideo(path: String) {
        imageView.visibility = View.GONE

        val player: ExoPlayer = ExoPlayer.Builder(requireContext()).build()

        videoPlayer.player = player
        val media = MediaItem.fromUri(path)
        player.addMediaItem(media)
        player.prepare()
    }

    private fun FragmentOpenItemBinding.showImageView(path: String) {
        videoPlayer.visibility = View.GONE
        ImageFun().setImage(imageView, path)
    }
}
